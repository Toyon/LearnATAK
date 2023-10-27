"""
This script is based off of the example script in the ml_training folder.

It sets up the YDS data pipeline, trains the model on the data, and 
evaluates performance. This script must be in the same directory as
YDS_export_tflite.py, which initializes the class and begins training.

See the ML guide for details on what much of this code is doing.

This script must be in the same directory as YDS_export_tflite.py, which
calls this class and executes training.
"""

import tensorflow as tf

from keras.preprocessing import image
from keras.preprocessing.image import ImageDataGenerator
from keras.models import Sequential
from keras.layers import Conv2D, MaxPooling2D, GlobalAveragePooling2D, Dense, Dropout
from keras.regularizers import l2

from sklearn.metrics import confusion_matrix, accuracy_score, roc_curve, auc

from typing import List
from PIL import Image, ImageDraw, ImageFont
from sklearn.model_selection import train_test_split
import numpy as np
import os

INPUT_SIZE = (256,256,3) 
NUM_CLASSES = 2

def inference(model, validation_data, class_labels, save_dir, max_pixel_value):
    if not os.path.exists(save_dir):
        os.makedirs(save_dir)

    for i, img in enumerate(validation_data):
        img_array = np.expand_dims(img, axis=0) / max_pixel_value
        prediction = model.predict(img_array)
        predicted_label = class_labels[np.argmax(prediction)]

        # Draw the inference result on the image as text.
        img_pil = image.array_to_img(img)
        draw = ImageDraw.Draw(img_pil)
        font = ImageFont.load_default()
        # TODO change font size
        draw.text((10, 10), str(predicted_label)[-6:], font=font, fill='red')

        img_pil.save(os.path.join(save_dir, f"annotated_{i}.png"))

class YDSModule(tf.Module):

    def __init__(self, learning_rate=0.001):
        super().__init__()

        self.model = Sequential([
            Conv2D(32, (3, 3), activation='relu', input_shape=INPUT_SIZE),
            MaxPooling2D((2, 2)),
            Conv2D(64, (3, 3), activation='relu'),
            MaxPooling2D((2, 2)),
            GlobalAveragePooling2D(),
            Dense(128, activation='relu', kernel_regularizer=l2(0.01)),
            Dropout(0.5),
            Dense(NUM_CLASSES, activation='softmax')
        ])

        model_plot_path = '/home/jvanveen/code/learnatak/ml_training/tflite/tflite_output_directory/YDSNet_plot.png'
        tf.keras.utils.plot_model(
            self.model,
            to_file=model_plot_path,
            show_shapes=True,
            show_dtype=False,
            show_layer_names=False,
            rankdir="TB",
            expand_nested=False,
            dpi=96,
            layer_range=None,
            show_layer_activations=True,
            show_trainable=False,
        )

        # Data preprocessing

        # Augmentation      
        AUG_SIZE = 2
        datagen = ImageDataGenerator(
            brightness_range=(0.5,1.5),
            fill_mode='nearest'
        )

        # Define the class folders and labels
        YDS_path = '/home/jvanveen/Downloads/YDS_Data/' 
        class_folders = ['class1_large_TEST_resize_256x256', 'class5_large_TEST_resize_256x256']
        class_folders = [YDS_path + class_ for class_ in class_folders]
        labels = np.arange(len(class_folders))

        # Load and preprocess the images
        images = []
        max_pixel_value = 0
        for folder in class_folders:
            image_files = os.listdir(folder)
            for image_file in image_files:
                image_path = os.path.join(folder, image_file)
                image = Image.open(image_path)
                # Ensure all input images are RGB
                if image.mode != 'RGB':
                    image = image.convert('RGB')
                image = np.array(image)

                images.append(image)
                for i in range(AUG_SIZE):
                    aug_image = datagen.random_transform(image)
                    images.append(aug_image)
                max_pixel_value = max(max_pixel_value, image.max())

        images = np.array(images)
        print(np.shape(images))

        # Create labels
        num_images = len(images)
        print(num_images)
        y = np.repeat(labels, num_images // len(class_folders)) 

        # 80 train - 10 test - 10 val split
        train_x, test_x, train_y, test_y = train_test_split(images, y, test_size=0.2)
        test_x, val_x, test_y, val_y = train_test_split(test_x, test_y, test_size=0.5)

        train_y = tf.keras.utils.to_categorical(train_y, num_classes=NUM_CLASSES)
        test_y = tf.keras.utils.to_categorical(test_y, num_classes=NUM_CLASSES)

        train_x = train_x / max_pixel_value
        val_x = val_x / max_pixel_value

        self.model.summary()

        optimizer = tf.keras.optimizers.Adam()
        self.model.compile(optimizer, loss='categorical_crossentropy')

        TRAINING_EPOCHS = 100
        self.model.fit(x=train_x, y=train_y, epochs=TRAINING_EPOCHS, validation_data=(test_x, test_y)) #, callbacks=[lr_scheduler])

        predicted_probs = self.model.predict(val_x)
        predicted_labels = np.argmax(predicted_probs, axis=1)

        # Evaluation metrics

        cm = confusion_matrix(val_y, predicted_labels)
        print("Confusion Matrix:")
        print(cm)

        accuracy = accuracy_score(val_y, predicted_labels)
        print("Accuracy:", accuracy)

        fpr, tpr, thresholds = roc_curve(val_y, predicted_probs[:, 1])
        roc_auc = auc(fpr, tpr)
        print("ROC AUC:", roc_auc)

        save_dir = "/home/jvanveen/code/learnatak/ml_training/tflite/tflite_output_directory/class-1-and-5-only/inference_results/"
        inference(self.model, val_x, class_folders, save_dir, max_pixel_value)

    @tf.function(input_signature=[
        tf.TensorSpec([1, 1], tf.float32)
    ])
    def get_weights(self, dummy_input):
        """Gets the weights from the dense layer

        Args:
            dummy_input: A dummy input, to avoid a warning about a function with zero inputs

        Returns:
             A map:
                {'dense': <tensor representing dense layer's weights>,
                'bias': <tensor representing bias weights>}
        """
        vars1: List[tf.Variable] = self.model.layers[0].weights
        dense = tf.convert_to_tensor(vars1[0])
        bias = tf.convert_to_tensor(vars1[1])
        return {'dense': dense, 'bias': bias}

    @tf.function(input_signature=[
        tf.TensorSpec([1] + list(INPUT_SIZE), tf.float32) 
    ])
    def predict(self, x: tf.Tensor):
        """Given an X tensor, predict Y from the model

        Args:
            x: Input to the model (expected size: INPUT_SIZE)

        Returns:
            Output from the model (expected size: DENSE_LAYER_SIZE)

        """
        y = self.model(x)
        return y
