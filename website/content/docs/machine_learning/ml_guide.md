---
title: "Machine Learning Development"
icon: icon/svg/tools.svg
description: An guide for creating ML models, integrating them into ATAK, and tips and tricks for model creation.
date: 2023-08-11T15:26:15Z
lastmod: 2023-08-11T15:26:15Z
draft: false
weight: 161
---


This document overviews the basics of Machine Learning (ML) needed for the typical ATAK plugin, outlines the ML / ATAK integration process, provides tips and tricks for getting started on a first ML project, and discusses the approach to more complicated and novel project ideas.


## ML Intro & Basics

Machine learning is about teaching computers to imitate how humans learn, see, and think. Well-known examples of ML in action include self-driving cars, ChatGPT, and DALL-E.

There are nearly endless applications, algorithms, and research available on ML that one could spend a lifetime studying. This overview will try to boil things down to only what you need to implement a basic ML-driven ATAK plugin. You can check out the Further Resources section to dive further into the topics mentioned as you progress in your project.

Here are a couple of important ML terms you should know:
- _Model:_ An algorithm that implements a certain way of doing machine learning [^1]. For example, the model that powers ChatGPT is a [Generative Pre-trained Transformer (GPT)](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwivr_Oa776AAxXehe4BHYq4BhoQmhN6BAhZEAI&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FGenerative_pre-trained_transformer&usg=AOvVaw0saeZQNnnPhRcQNK91AJdL&opi=89978449). Models "learn" by operating on the data they are given.
- _Dataset:_ All the data you give the model to learn from. Having a large enough dataset with good-quality data is crucial for a model to learn correctly. A single unit of the data (e.g. a single image or a string of text) is called a _sample_. 
- _Class:_ The category a sample in the dataset belongs to. In many problems [^2], the differences between classes determines how the dataset is structured. For example, if you were trying to train a model to distinguish between images of cats and images of dogs, you would have two classes: class "cat" and class "dog". Then if your data is organized in a folder structure, all images of cats would be in one sub-folder, and all images of dogs would be in another sub-folder.
- _Feature:_ A pattern in the dataset that the model learns as it sees the data, and can use to figure out which class a sample of the data is part of. In our dog/cat example, a model might learn that dogs typically have longer snouts than cats, and could use that pattern to tell if a new image was of a dog or a cat. We would then call "snout length" a feature.

Here is an outline of the process an ML model takes in its lifetime: 
- During _training,_ a model takes in data from a dataset and tries to learn features from the dataset.[^2] 
- During _testing,_ you investigate how well the model has learned by seeing how it categorizes new samples of data that it didn't see during training. This will inform you of ways you can change the data or the model so the model learns better.
- During _inference,_ the model is finished learning and performs the task you want to use it for. Inference is what your model will be doing when you deploy it in your ATAK plugin.

ML models can be notoriously difficult to train. The best models are trained on _millions_ of samples of data and are finely tuned by experts in the field. Luckily, most of the difficult work is already done for you, thanks to existing pre-trained models and helpful software packages. These days it just takes a couple lines of code to perform inference with a pre-existing model and dataset.

The dominant language used in ML is [Python](https://www.python.org/). This language is relatively easy to read and learn for novice programmers. There are many commonly-used ML software libraries written in Python that will automate much of the ML process for you.

## ML Development / ATAK Plugin Integration Process

Here is the general process you'll follow for developing an ML plugin. Steps that require code will show some example code from this script (# TODO add link) with comments to demonstrate how you might implement this in code.

- **Choose a problem you want to solve.** In ATAK, you will almost always be performing image-based ML. Most plugins will have the user take an image of an object or natural feature in the environment, classify that object in a desired way, and possibly present information or recommendations to the user. For example, a plugin could recognize a symbol or pictogram in a photo taken of a chemical hazard label and return instructions describing how the user should handle the chemical. Don't just pick any problem--make sure it's something that ATAK would be useful for. Note that solving some ML problems may be much more difficult and time-consuming than others (see [this section](#ml-problems-datasets-and-models-from-scratch) below).
- **Check out the example plugins.** This repo contains several plugins that will give you an idea of a problem to solve, how to implement your idea, a project structure to follow, and more.
- **Find the dataset you want to use.** Thousands of datasets are freely available online. You might start by Googling the kind of data you're looking for, or checking out some popular dataset hub websites.[^3]
- **Select the model you want to use.** The model you select depends on the task you're trying to accomplish. Simple image classification is probably the most common and easiest task to start with. However, your problem may require a different approach.[^4]
- **Preprocess your data.** This essential step processes the data into a form that the model can use. For image datasets, it is standard to change image resolutions, balance the number of images in each class, and perform augmentation to artificially increase the size and diversity of your dataset. You can read more about augmentation in the next section. Below is an example of what this might look like. 
```python
# Import required packages
from keras.preprocessing.image import ImageDataGenerator
import numpy as np
from sklearn.model_selection import train_test_split

# Augmentation      
# AUG_SIZE is how many times bigger the augmented dataset will be compared to the original dataset.
AUG_SIZE = 5 
# An ImageDataGenerator is an object that takes in your raw dataset and applies transformations.
# You can read more about available transformations here:
# https://www.tensorflow.org/api_docs/python/tf/keras/preprocessing/image/ImageDataGenerator
datagen = ImageDataGenerator(
    width_shift_range=0.2,
    height_shift_range=0.2,
    horizontal_flip=True,
    vertical_flip=True,
    brightness_range=(0.5,1.5),
    zoom_range=0.2,
    fill_mode='nearest'
)

# Define the class folders and labels.
# Your data will be stored somewhere. You need to point to where that
# is to load in your data. This code assumes a file structure where
# each class has its own folder.
data_path = '/home/jvanveen/Downloads/YDS_2class_224x224/'
class_folders = ['class1', 'class2'] 
# Create paths that look like: '/home/jvanveen/Downloads/YDS_2class_224x224/classn'
class_folders = [data_path + class_ for class_ in class_folders]
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

                # Convert images to NumPy array
                images = np.array(images)
                print(np.shape(images))

                # Create labels
                num_images = len(images)
                print(num_images)
                num_classes = len(class_folders)
                y = np.repeat(labels, num_images // len(class_folders)) 

                # Train - test - validation split
                # Most of your data (80% is standard) should be used for training.
                # The remaining 20% should be used for testing. You will see how the model
                # performance improves on the test data over time during training. 
                # The 'x' terms hold the actual data, while the 'y' terms hold the labels.
                train_x, test_x, train_y, test_y = train_test_split(images, y, test_size=0.2)
                test_x, val_x, test_y, val_y = train_test_split(test_x, test_y, test_size=0.5)

                # This code turns the labels into what is called one-hot encoding. You don't have
                # to know what that means. Just remember that many models will require labels
                # to be set up like this for training.
                train_y = tf.keras.utils.to_categorical(train_y, num_classes=NUM_CLASSES)
                test_y = tf.keras.utils.to_categorical(test_y, num_classes=NUM_CLASSES)

                # Normalize the pixel values. This scales the pixel values from a range of 
                # 0 - 256 to a range of 0 - 1. This may help the model learn better.
                train_x = train_x / max_pixel_value
                val_x = val_x / max_pixel_value
```
- **Create your model.** It's recommended to use one of the many pre-trained models that come with ML libraries like Keras and Tensorflow. These models can often be created with a single line of code. However, you may have to try out different pretrained models. [^5]

```python
# Import required packages
import tensorflow as tf
from tensorflow import keras

# Your model needs to know the size of the training data it's getting and the number
# of classes in your problem. 224x224 is a standard input size for many models.
# 224,224 is the size of your images in pixels. 3 indicates these are RGB (color) images.
INPUT_SIZE = (224,224,3) 
NUM_CLASSES = 2

# This code creates a new instance of a model. You don't have to know what all these
# arguments mean. You just need to make sure the input shape and number of classes 
# are set correctly.
model = keras.applications.MobileNetV3Small(
    input_shape=INPUT_SIZE, 
    alpha=1.0,
    dropout_rate=0.2,
    include_top=False,
    weights="imagenet",
    input_tensor=None,
    pooling=None,
    classes=NUM_CLASSES,
    classifier_activation="softmax",
    include_preprocessing=True
)

# You can read more about this model here:
# https://www.tensorflow.org/api_docs/python/tf/keras/applications/MobileNetV3Small
```
- **Train your model.** You will have to feed the model your data in a certain way to train it. Then you will call a command like `.fit` to begin training. Online tutorials usually demonstrate how to do this in code, and you can also check out the code in this repo. Training may take a long time depending on how big your model and your dataset are.

```python
# This will print a detailed description of the model structure to your console.
model.summary()

# Don't worry about what this code does. It is required for model training.
optimizer = tf.keras.optimizers.Adam()
model.compile(optimizer, loss='categorical_crossentropy')

# The number of "epochs" is the number of times your model will operate over
# all the training data.
TRAINING_EPOCHS = 20 
# This command performs the actual training.
# Once your code hits this command, you will start to see output on the console
# as your model trains.
model.fit(x=train_x, y=train_y, epochs=TRAINING_EPOCHS, validation_data=(test_x, test_y))
```
- **Investigate how well your model learned.** There are several ways of doing this, so this is just to get you started. As training is going on, you will see a `loss` and `val_loss` or something similar for each epoch. Pay special attention to `val_loss`, as this is calculated over your test data, which was not seen during training. The smaller this number is, the better. If this number becomes large, it is a sign your model is not learning well. 
    - Another common test is called a confusion matrix. All you need to know for now about a confusion matrix is that you want larger numbers on the diagonal of the matrix and smaller numbers everywhere else. 
    ```python
    from sklearn.metrics import confusion_matrix

    # Get the model's predictions on data it hasn't seen before.
    predicted_probs = model.predict(val_x)

    # This converts predictions into labels for the confusion matrix.
    predicted_labels = np.argmax(predicted_probs, axis=1)

    # Compute the confusion matrix.
    cm = confusion_matrix(val_y, predicted_labels)

    print("Confusion Matrix:")
    print(cm)

    # A good confusion matrix will look something like this.
    # The matrix is always square. In this example, there are 5 classes,
    # so the matrix will have 5 rows and 5 columns. 
    [[98 0  1  0  0]
     [0  99 0  2  0]
     [0  0  98 0  0]
     [1  0  0  97 0]
     [0  0  0  1  99]]
    ```
    - Other ways of measuring model performance can be found in the next section.
- **Save your trained model and export it to Android Studio.** Once you are satisfied with your model performance and want to test it out in your TAK plugin, you can export it into the `.tflite` format. This format is an efficient, mobile-friendly means to run inference on your device. Follow the documentation [here](https://github.com/Toyon/LearnATAK/tree/master/ml_training/tflite/README.md) and the corresponding Python scripts for how to do it.

If you want another end-to-end guide, check out [this tutorial](https://www.tensorflow.org/tutorials/customization/custom_training_walkthrough) for a guide on what the basic ML process looks like in code. You don't have to understand all of the concepts or math they use. 

Alternatively, if you just want to work on the Android Studio development side and skip ML development entirely, check out the [TensorFlow Hub](https://tfhub.dev/), which has pre-trained versions of hundreds of different ML models. Here you can simply choose an ML problem your plugin will solve and grab a corresponding model. All you'll have to do in Python is convert the pre-trained model (typically a `.pb` file) to `.tflite` by following the [tflite doc](../../example_plugins/tflite-training-readme/). 

## ML Tips & Tricks

The following are some tips and tricks for ML development and training. 

- **Avoid overfitting.** [Overfitting](https://www.ibm.com/topics/overfitting) occurs when the model can't learn general features well enough from training data. Because the model can only learn from the data you give it during training, your training data needs to represent all the variety of data your model may encounter "in the wild" during inference. If your data doesn't represent the "in the wild" possibilities well enough, your model will learn features that only exist in the training data and not "in the wild", leading to worse performance. You can reduce overfitting by using image augmentation and transfer learning, described below. 
- **Image augmentation.** Augmentation artificially increases the size and diversity of your dataset. It does so by applying transformations to the image (e.g. flips, rotations, [shears](https://hasty.ai/docs/mp-wiki/augmentations/shear), zooming, brightness/contrast adjustments, etc.) and adds the transformed images to the data going into your model. This helps combat overfitting by 1) giving the model more data to train on, and 2) helping the training dataset better represent the real-world "in the wild" data by making it more diverse. Unless your raw dataset is especially large (say 100,000+ images), you will generally need to use augmentation. The sample code above uses augmentation.
- **Transfer learning.** ML models are commonly pre-trained on another dataset before they encounter your dataset. Re-using knowledge in this way is called _transfer learning_[^6]. This can drastically improve model performance and reduce training time. You will almost always be performing transfer learning in ATAK projects. A standard dataset that models are pretrained on is [Imagenet](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwjl4LOqs8GAAxWMJUQIHetLDnwQFnoECBAQAQ&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FImageNet&usg=AOvVaw0CTOStR4n_jXUz_oFObTIK&opi=89978449), which is a very large and generalized image dataset. In the sample code above, the model is defined with `weights="imagenet"`, meaning the model has been pretrained on Imagenet.
- **Class balancing.** To ensure each class is equally represented in what your model is learning, you should have the same number of samples in each class. Imbalanced classes can lead to overfitting. 
- **Techniques for measuring performance.** Here are some common techniques for measuring model performance on testing data. You will not need to use all of them, and some may not be applicable to your problem. It is important to know how and whether they will work in your problem before using them, so ask for help if you're having trouble understanding them.
    - [Confusion matrix](https://www.geeksforgeeks.org/confusion-matrix-machine-learning/) (used in sample code above)
    - [Accuracy score](https://developers.google.com/machine-learning/crash-course/classification/accuracy)
    - [Precision, Recall, & f1 Score](https://towardsdatascience.com/a-look-at-precision-recall-and-f1-score-36b5fd0dd3ec) (overlaps with Accuracy & Confusion Matrix)
    - [ROC Curves & AUC](https://developers.google.com/machine-learning/crash-course/classification/roc-and-auc)
    - [Mean Absolute Error (MAE) and Mean Squared Error (MSE)](https://towardsdatascience.com/understanding-the-3-most-common-loss-functions-for-machine-learning-regression-23e0ef3e14d3)
    
    For example, here is sample code for a confusion matrix, accuracy score, and AUC:
    ```python
    from sklearn.metrics import confusion_matrix, accuracy_score, roc_curve, auc

    # Each method compares the actual labels (val_y) to the model's prediction (predicted_labels)
    cm = confusion_matrix(val_y, predicted_labels)
    print("Confusion Matrix:")
    print(cm)

    accuracy = accuracy_score(val_y, predicted_labels)
    print("Accuracy:", accuracy)

    fpr, tpr, thresholds = roc_curve(val_y, predicted_probs[:, 1])
    roc_auc = auc(fpr, tpr)

    ```
- **Visualize model performance.** The techniques above are most helpful when their results can be visualized. You can plot many performance metrics over time as your model trains, which helps you understand what is happening better than just looking at numbers.
- **Use a small enough model.** A model's "size" is how many parameters it contains. You can think of "parameters" as terms in a system of equations: the more terms there are, the more complicated the system is and the more computing power it takes to run. Because mobile devices that use ATAK have limited computing power, you need to use an efficient model that will not slow down the device. [MobileNetV3](https://www.tensorflow.org/api_docs/python/tf/keras/applications/MobileNetV3Small), used in the sample code above, is an example of an efficient model designed for mobile devices.
- **Know when to stop training.** If you train for too long, your model will overfit. How many epochs you should train for is a tough balance to get right. There are good rules of thumb you can use if you are using a pre-trained model on a pre-existing dataset. For example, you probably should not train past 20 epochs on a new dataset if you are using a model pretrained on Imagenet. This may vary from problem to problem though, so do your research. If the rules of thumb aren't helping, there are also "early stopping" techniques that directly modify the training process<sup>7</sup>. 

## ML Problems, Datasets, and Models from Scratch

This is an advanced section that you should skip for now if this is your first time designing a plugin. Note that it will  be helpful to have some ML experience and knowledge for this section.

Solving a totally new problem and building a new dataset from scratch can be exciting and rewarding, but it is typically much more difficult and time-consuming than using preexisting ideas and data. This approach is NOT recommended for designing your first working plugin. Only do this if you have a lot of time and motivation to see the project through. Do not expect to perfectly implement the idea in your head--you will almost surely need to simplify the problem scope as you go and accept less than state-of-the-art performance. With that said, you'll learn many new things along the way, which is a reward in itself even if you can't get your project to work as well as you'd hoped.

One of the plugins in this repo tries to solve a new problem, and uses a new dataset built from scratch. You can read about the development process [here](https://github.com/Toyon/LearnATAK/-/blob/yds-estimator/YDS-Estimator/doc/ML_Dev_doc.md). This should give you an idea of the difficulties involved in defining a new problem and curating a dataset that can actually solve the problem.

Below are some tips for getting started on this approach.

#### Defining a new problem
Here are some questions to ask yourself. Your answers may take time to develop as you research the problem, but they should be specific and well-defined before proceeding with implementation.
- What unaddressed needs are there that your plugin could solve? Who would find this plugin useful?
- Where will your data come from? How much data can you expect to gather given your time/resources?
- What kind of model will you use? How might you have to adjust your problem scope to work with preexisting models?
- Have other people tried to solve similar problems? Can you use lessons they learned or parts of their data or code?

#### Gathering and Curating Data
- You may be able to build your dataset by starting from an existing dataset.[^3] If you build your dataset entirely from scratch, you will have to manually examine and label every sample. This is very time-consuming and will probably yield a relatively small dataset.
- For image datasets, you may be able to find or record videos of the objects you want to classify, then extract the frames from the videos and use the frames as images in your dataset. This can mitigate the small-dataset problem. For example, a single 10-second video of a cat walking at 30 fps would give you 300 images for "cat" classification.
    - The drawback of this method is that you will have many very similar images of the same object, and will risk overfitting. You should include frames from many different videos, capturing the variation within each class as best you can. When evaluating performance, make sure your validation dataset does not contain only frames that are very similar to frames that were trained on.
- For image datasets, consider what aspects of the image contribute to the features your model will learn. For example, you would need color channels in an image dataset to distinguish between granny smith apples and red delicious apples.
- Consider the kind of preprocessing and augmentation you will have to apply to your dataset. 
    - Most models require images to be resized to a square resolution. If the raw images are not square, the processed images will be distorted, and this may cause feature degradation (e.g. a full-body landscape image of a dog is "squished" during processing and is misclassified as a cat).
    - Some image augmentation transformations should not be used on your data. For example, say your model needs to distinguish between square-shaped objects and rectangular-shaped objects to classify accurately. Augmenting with shears would distort the square-shaped objects into more rectangular-looking shapes and prevent the model from learning the distinction it needs to know. Knowing which transformations to apply depends on your conceptual understanding of the problem.
- Make sure the classes in your dataset are as balanced as possible. This will mean the size of each class will be limited to the size of the class you gathered the least data for. However, this is a worthwhile trade to mitigate overfitting.
- Lastly, remember that the amount and quality of the data you can gather may make or break model performance. A model is only as good as the dataset you can give it.

#### Model Building, Selection and Tuning
- You should first try existing, pretrained state-of-the-art models. There is a good chance that the feature space they learn will be sufficient for your task. However, depending on the nature of your dataset, you may try using a custom architecture and training from scratch.[^5]
    - If you do go this route, there are many standard features a model designed for a certain task will have. An image classification model will always have 2D convolutional layers, for example. You should research what common features a model designed for your task will have.

#### Measuring and Improving Performance
- You will probably have overfitting problems. Make sure your model uses [regularization techniques](https://www.simplilearn.com/tutorials/machine-learning-tutorial/regularization-in-machine-learning#what_is_regularization_in_machine_learning).
- Make use of as many performance metrics as you can. Not all of them will apply to your problem. However, because you are working on a new problem and/or with a new dataset, you may have to try several different techniques to see which ones deliver the most insight on performance. 
- Simplify the problem. For example, if you are having trouble with a 5-class dataset, try just reducing it to the two classes that seem most distinct, then train again and see if performance improves.
- Do a sanity check by measuring performance with the training data. If the model is actually learning from the training data, its performance should be very good on that same data. If it isn't, there is probably another problem beyond just overfitting.
- Take full advantage of "early stopping" techniques[^7].

As a final note, designing an entirely new model architecture from scratch is far beyond the scope of an ATAK plugin. Even the top experts in the field spend months or years producing new breakthroughs in model design. You should not have to invent the next GPT or YOLO in order to complete the project.

## Learning More About ML
This section discusses how you can start learning more about machine learning beyond what you need for ATAK. 

First off, the most important thing you can do to learn any new skill is to **work lots of problems.** Work math problems to improve your math skills. Implement some function or algorithm in Python to work your programming skills. Work on ML projects to improve your knowledge of ML. You won't truly learn just by reading your math textbook or watching a Youtube video about ML. It's difficult and takes time, but working real problems is the only way to really develop a new skill. If you're working with instructors, ask them for math, programming, or other problems to work that are appropriate for your skill level. 

- **Practice your math skills.** Fully understanding how ML works requires a very strong mathematical background. College graduate-level courses in topics such as linear algebra, matrix methods, and probability/statistics cover all the math that happens under the hood in your model. However, there is still a lot you can learn about the nuts & bolts of ML without being a college-level math whiz. 

    If you want to dip your toes into the math behind ML, we suggest checking out 3Blue1Brown's excellent video series on [Calculus](https://www.3blue1brown.com/topics/calculus), [Linear Algebra](https://www.3blue1brown.com/topics/linear-algebra), [Probability](https://www.3blue1brown.com/topics/probability), and [Neural Networks](https://www.3blue1brown.com/topics/neural-networks). These videos have great explanations and visuals for foundational ML math topics. Even if these topics don't seem to directly connect to ML now, they will be essential for any science or engineering field you might pursue in the future. However, remember that the only way to really learn this math is to work problems. If you are working with instructors, you should ask them for suggestions about what kind of math is appropriate for your skill level.

- **Learn how to code and practice programming skills.** You don't need to be a coding expert to perform many ML tasks, but the more programming you learn will open up more avenues for ML exploration. Python is a great first language to learn, and understanding its fundamentals will get you far in working with ML. Key Python concepts you could look further into are:
    - Basic Python structure and syntax
    - Iterative and conditional programming (`for`, `if`, and `while`)
    - Python functions
    - Data types and structures (lists/arrays, dictionaries)
    - Math operations in Python (functions, vectors, matrices)
    - Math, data manipulation, and ML libraries in Python (Numpy, Pandas, Scikit-learn, Tensorflow)

    Again, consult with your instructors on what sort of programming concepts would be appropriate for you.

- **Get familiar with different programming & development environments.** Jupyter notebooks through services such as Google Colab are great for learning ML coding concepts and are very easy to work with. You may be able to use them to 

- **Work on projects.** The best way to learn any new skill is to actually use it how you would "in the real world". If you have followed the lessons in this repo and built an ML model into an ATAK plugin, you're already on track. From here you could focus on ML projects outside of ATAK. # TODO link suggestions for places to get projects/project ideas

This repo has another document that introduces ML concepts [here](../ml_overview/). 


### Comprehensive Courses
You can learn almost anything about ML from freely available online resources. However, learning about ML in an organized, step-by-step format will be much more helpful than bouncing from one article to another on the internet. Below are some introductory courses we recommend. Because ML is such a rapidly developing and changing field, we suggest you try courses that are either newer or regularly updated.
- [Andrew Ng's intro course to ML](https://www.coursera.org/specializations/machine-learning-introduction?action=enroll&adgroupid=46849728719&adposition=&campaignid=685340575&creativeid=606098666387&device=c&devicemodel=&gclid=Cj0KCQjw0bunBhD9ARIsAAZl0E3wCrCuLD3gOQ1JTPPCHAGsolj1hn2DEPxqv7hE2jiVK0BexK3FgloaAj7REALw_wcB&hide_mobile_promo&keyword=andrew+ng+machine+learning&matchtype=b&network=g&utm_campaign=B2C_NAMER_machine-learning-introduction_stanford_FTCOF_specializations_country-US-country-CA&utm_medium=sem&utm_source=gg) teaches both ML concepts and programming skills at the same time.
- [Microsoft's Python for Beginners](https://learn.microsoft.com/en-us/training/paths/beginner-python/) course will step you through Python fundamentals.
- [A list of free ML courses from many top universities](https://www.classcentral.com/subject/machine-learning?free=true). Make sure a course is appropriate for your skill level and learning goals before diving in.

### Other Useful Learning Resources
- [A step-by-step beginner ML tutorial](https://machinelearningmastery.com/machine-learning-in-python-step-by-step/) that walks through importing libraries, working with data, and tests different algorithms.
- Note 4 links to a simple image classification tutorial in Tensorflow.[^4]


## Notes & Further Resources
These notes and resources are not necessary to get started with ML in an ATAK plugin, but may prove helpful for further research.

[^1]: The ML models you will typically use in ATAK are called [neural networks](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwjWsaCf276AAxUTJUQIHRoLDnYQmhN6BAhWEAI&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FArtificial_neural_network&usg=AOvVaw0CMek9gs4Tdr7H_YcH9z1H&opi=89978449), which are algorithms that try to imitate the human brain. Check out [3Blue1Brown's excellent intro video to neural networks](https://www.youtube.com/watch?v=aircAruvnKk) if you want to learn more. You don't need to worry about the small amount of math he uses.
[^2]: There are two variations on learning from data. _Supervised learning_ uses labels that are assigned to each sample of data before the model sees it. For example, the model would get the image of a cat with a label indicating "cat". These labels make the problem easier to solve for the model, but require humans to label the data ahead of time. <br>On the other hand, _unsupervised learning_ tries to learn features and make predictions without labels being assigned beforehand. Unsupervised learning is typically a harder problem, but does not need a human to label the data ahead of time. You will likely only use supervised learning in your project.
[^3]: Some of the best dataset hubs include [Hugging Face](https://huggingface.co/datasets) and [Kaggle](https://www.kaggle.com/datasets). A select few small datasets can be loaded directly from software packages, such as [the iris dataset](https://scikit-learn.org/stable/auto_examples/datasets/plot_iris_dataset.html).
[^4]: TensorFlow has a great [image classification tutorial](https://www.tensorflow.org/tutorials/images/classification). You will probably not have to build a model layer-by-layer like they do here. Instead you will want a small and efficient model, such as [MobileNetV3](https://www.tensorflow.org/api_docs/python/tf/keras/applications/MobileNetV3Small) for image classification or YOLO for object detection (used in the [democnn plugin](https://github.com/Toyon/LearnATAK/-/tree/main/democnn)). Oftentimes these pretrained models can be imported from packages with a single line of code.
[^5]: Below is an example custom architecture designed for image classification, used in this project. # TODO link to project 

    ```python
            from keras.models import Sequential
            from keras.layers import Conv2D, MaxPooling2D, GlobalAveragePooling2D, Dense, Dropout
            from keras.regularizers import l2
    
            INPUT_SIZE = (256,256,3)
    
            model = Sequential([
                Conv2D(32, (3, 3), activation='relu', input_shape=INPUT_SIZE),
                MaxPooling2D((2, 2)),
                Conv2D(64, (3, 3), activation='relu'),
                MaxPooling2D((2, 2)),
                GlobalAveragePooling2D(),
                Dense(128, activation='relu', kernel_regularizer=l2(0.01)),
                Dropout(0.5),
                Dense(NUM_CLASSES, activation='softmax')
            ])
    ```

[^6]: Check out [this tutorial](https://www.tensorflow.org/tutorials/images/transfer_learning) for an end-to-end example on transfer learning. You may even have to build your own model from scratch, which is thankfully less daunting than it sounds. 
[^7]: Here are some common techniques.<br>- Monitoring Validation Loss: Track the validation loss (loss on the testing data) during training. If the loss stops improving or starts to increase for a certain number of epochs, stop training.<br>- Patience Parameter: Define a "patience" parameter that determines how many epochs the model can continue training without improvement in the validation loss. If the loss does not improve for the specified number of epochs, stop training.<br>- Model Checkpointing: Save the model's weights whenever the validation loss improves. Then continue training and only use the saved weights of the best-performing model at the end<br>- Learning Rate Reduction: Reduce the learning rate (how fast the model moves towards the theoretical solution) when the validation loss stops improving. This may help the model get "unstuck" from an overfitting "rut".

