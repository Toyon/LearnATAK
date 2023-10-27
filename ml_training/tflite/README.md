# TensorFlow Lite

## Setup

**Optional**:

We recommended you use a IDE designed for Python development, such as [PyCharm Community Edition](https://www.jetbrains.com/pycharm/) (which is free!)

**Requirements:**

1. A system that is compatible with TensorFlow. This includes most computers, with some caveats:
Older CPUs may not be compatible with TF at all. Systems without a dedicated NVIDIA graphics card can still use TF, but hardware acceleration and some features may be limited.

2. An installation of Python, versions 3.8 - 3.11

3. An installation of TensorFlow, using `pip`. The current recommended version is 2.10.0

```
pip install tensorflow==2.10.0
```

If you have an older version of `pip`, you may have to update that before installing TF:

```
pip install --upgrade pip
```

However, if you have an NVIDIA graphics card and want to use hardware acceleration (which is optional), you will have to perform some additional steps. Check instructions for your operating system at [TensorFlow's Installation Page](https://www.tensorflow.org/install/pip#step-by-step_instructions)

## TensorFlow Functions

When exporting this module, you can specify functions to attach to the exported TF Lite model. These functions are then executable on other platforms.
TF Functions are decorated with `@tf.function`, and should specify an `input_signature` that defines the size and type of each argument. The file `example_module.py` includes 3 example TF lite functions.

Read the official docs for more info on [using `tf.function`](https://www.tensorflow.org/guide/intro_to_graphs?hl=en#using_tffunction) in your code for best practices and a better understanding of the benefits. There is also a [guide for better performance with `tf.function`](https://www.tensorflow.org/guide/function).

## Example Module (example_module.py)

`example_module.py` defines `ExampleModule`, a `tf.Module` that contains a model. This example module demonstrates how to design a model and define TF functions that exposes information (gets the model's weights) and  performs operations (performing inference and setting weights).

Read the comments for more details. Training the model is commented out, since this project does not include a dataset, but instructions for an example training step are in the constructor.

### Exporting Example Module

The file `main_export_example_tflite.py` shows how to export a `tf.Module` to a TFLite file.

First, a dictionary maps the function names to the module's TF functions. The module is saved as a `SavedModel` directory format in an intermediate step. Then, the `SavedModel` format is read and converted into a single `*.tflite` file. An example of what this `SavedModel` directory looks like is in `/ml_training/yds_estimator_training/saved_model_directory`.

If you generate a model with a custom training script (e.g. `YDS_trainer.py`), you may run into an input dimensions error when deploying the exported model in Android Studio. To resolve this issue, manually set the input size model signature and then convert back to a `tflite` model. An example code snippet from `YDS_export_tflite.py` to do this is below:
```python
    model = tf.saved_model.load(saved_model_dir) 
    model.signatures["predict"].inputs[0].set_shape([1, 256, 256, 3])
    tf.saved_model.save(model, "saved_model_updated", signatures=model.signatures["predict"])
    converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir='saved_model_updated', signature_keys=['serving_default'])
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS, tf.lite.OpsSet.SELECT_TF_OPS]
    tflite_model = converter.convert()
```

Read the code and comments for more details.
