"""
    Exporting YDSModule to a tflite file.

    Run this while in the `tflite` directory:
        python main_export_example_tflite.py

    Expected results:
        A file, `example.tflite`, will be inside `tflite_output_directory`
    
    This script must be in the same directory as YDS_trainer.py for training
    to work as intended. To train, in terminal navigate to the directory where 
    YDS_trainer.py and YDS_export_tflite.py are located, then run this 
    command: python YDS_export_tflite.py
"""
import os

import tensorflow as tf

from YDS_trainer import YDSModule

def export_YDS_module(saved_model_dir='/home/jvanveen/code/learnatak/ml_training/yds_estimator_training/saved_model_directory',
                          output_dir='tflite_output_directory/class-1-and-5-only'):
    """Converts and saves a TFLite model for YDSModule.

    example.tflite will be saved in `output_dir`

    Args:
        output_dir: A directory path to save the TFLite model.
        saved_model_dir: A directory path to save the intermediate SavedModel type
    """

    output_dir = '/home/jvanveen/code/learnatak/ml_training/tflite/' + output_dir
    os.makedirs(output_dir, exist_ok=True)
    # Call the training module to create and train the model
    model = YDSModule()

    # First, the model is exported as a `SavedModel`- an intermediate format that TF uses
    tf.saved_model.save(
        model,
        saved_model_dir,
        # Here, define the TF functions that are exported:
        signatures={
            'get_weights': model.get_weights.get_concrete_function(),
            'predict': model.predict.get_concrete_function(), 
        })

    # Reload the model from directory format to manually set input size model signature.
    # This is required when manually exporting a custom model using this script.
    model = tf.saved_model.load(saved_model_dir) 
    model.signatures["predict"].inputs[0].set_shape([1, 256, 256, 3])
    tf.saved_model.save(model, "saved_model_updated", signatures=model.signatures["predict"])
    converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir='saved_model_updated', signature_keys=['serving_default'])
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS, tf.lite.OpsSet.SELECT_TF_OPS]
    tflite_model = converter.convert()

    # Save the model with correct model signatures
    model_file_path = os.path.join(output_dir, 'YDS_256x256_2class_YDSNet_100-epochs_augsize2.tflite')
    with open(model_file_path, 'wb') as model_file:
        model_file.write(tflite_model)

    # Check the input/output to confirm the new signature was applied correctly.
    # You may comment out the below lines if you don't need to see this info
    interpreter = tf.lite.Interpreter(model_content=tflite_model)
    interpreter.allocate_tensors()

    # Get input and output tensors.
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    print("\nInterpreter input details: ")
    print(input_details)
    print("\nInterpreter output details: ")
    print(output_details)

    input_shape = input_details[0]['shape']
    print("\nInput shape: ")
    print(input_shape)

if __name__ == '__main__':
    export_YDS_module()
