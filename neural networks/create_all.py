import os
import os.path

from keras.models import Model, load_model
from keras.layers import Input, Dense, concatenate
from keras.optimizers import SGD
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.utils.class_weight import compute_class_weight


DATA_PATH = '/Users/linusstenzel/Desktop/uni/CAI/data'
MODEL_PATH = '/Users/linusstenzel/Desktop/uni/CAI/ai/models'

MODEL_NAMES = ['draw', 'meld_if', 'meld_value',
               'meld_wild', 'add_if', 'add_value', 'add_wild', 'drop']

####################################################
#-----------------------DRAW-----------------------#
####################################################


def buildDraw():
    draw_input = Input(shape=(70,), name='input')
    x = Dense(64, activation='relu')(draw_input)
    x = Dense(32, activation='relu')(x)
    x = Dense(16, activation='relu')(x)
    draw_output = Dense(1, activation='sigmoid', name='output')(x)
    return Model(inputs=draw_input, outputs=draw_output)


def trainDraw():
    print("training draw")

    train_test = train_test_data('draw.csv', 70, 1, 0.005)
    x_train = train_test['x_train']
    y_train = train_test['y_train']

    draw = buildDraw()

    class_weight = compute_class_weight(
        'balanced', np.unique(y_train.flatten()), y_train.flatten())
    d_class_weight = dict(enumerate(class_weight))

    draw.compile(optimizer=SGD(lr=0.01),
                 loss='binary_crossentropy',
                 metrics=['accuracy'])
    draw.fit(x_train, y_train, epochs=150,
             batch_size=256, class_weight=d_class_weight)

    draw.save(MODEL_PATH + "/draw.h5")
    return draw


def evaluateDraw(do_training):
    draw = None
    if do_training:
        draw = trainDraw()
    else:
        draw = load_model(MODEL_PATH + "/draw.h5")

    train_test = train_test_data('draw.csv', 70, 1, 0.005)
    x_test = train_test['x_test']
    y_test = train_test['y_test']

    results = draw.evaluate(x_test, y_test, batch_size=16)
    evaToFile(results, 'draw')

    predictions = draw.predict(x_test[::4])
    truth = y_test[::4]
    savePredTruth(predictions, truth, "draw.csv", 1)


####################################################
#----------------------MELD_IF---------------------#
####################################################


def buildMeldIf():
    meld_if_input = Input(shape=(70,))
    x = Dense(64, activation='relu')(meld_if_input)
    x = Dense(32, activation='relu')(x)
    x = Dense(16, activation='relu')(x)
    meld_if_output = Dense(1, activation='sigmoid')(x)
    return Model(inputs=meld_if_input, outputs=meld_if_output)


def trainMeldIf():
    print("training meld_if")

    train_test = train_test_data('meld_if.csv', 70, 1, 0.005)
    x_train = train_test['x_train']
    y_train = train_test['y_train']

    meld_if = buildMeldIf()

    class_weight = compute_class_weight(
        'balanced', np.unique(y_train.flatten()), y_train.flatten())
    d_class_weight = dict(enumerate(class_weight))

    meld_if.compile(optimizer=SGD(lr=0.01),
                    loss='binary_crossentropy',
                    metrics=['accuracy'])
    meld_if.fit(x_train, y_train, epochs=150,
                batch_size=256, class_weight=d_class_weight)

    meld_if.save(MODEL_PATH + "/meld_if.h5")
    return meld_if


def evaluateMeldIf(do_training):
    meld_if = None
    if do_training:
        meld_if = trainMeldIf()
    else:
        meld_if = load_model(MODEL_PATH + "/meld_if.h5")

    train_test = train_test_data('meld_if.csv', 70, 1, 0.005)
    x_test = train_test['x_test']
    y_test = train_test['y_test']

    results = meld_if.evaluate(x_test, y_test, batch_size=16)
    evaToFile(results, 'meld_if')

    predictions = meld_if.predict(x_test[::4])
    truth = y_test[::4]
    savePredTruth(predictions, truth, "meld_if.csv", 1)

####################################################
#--------------------MELD_VALUE--------------------#
####################################################


def buildMeldValue():
    meld_value_input = Input(shape=(70,))
    x = Dense(64, activation='relu')(meld_value_input)
    x = Dense(32, activation='relu')(x)
    x = Dense(16, activation='relu')(x)
    meld_value_output = Dense(11, activation='softmax')(x)
    return Model(inputs=meld_value_input, outputs=meld_value_output)


def trainMeldValue():
    print("training meld_value")

    train_test = train_test_data('meld_value.csv', 70, 11, 0.01)
    x_train = train_test['x_train']
    y_train = train_test['y_train']

    meld_value = buildMeldValue()

    meld_value.compile(optimizer=SGD(lr=0.01),
                       loss='categorical_crossentropy',
                       metrics=['accuracy'])
    meld_value.fit(x_train, y_train, epochs=150, batch_size=64)

    meld_value.save(MODEL_PATH + "/meld_value.h5")
    return meld_value


def evaluateMeldValue(do_training):
    meld_value = None
    if do_training:
        meld_value = trainMeldValue()
    else:
        meld_value = load_model(MODEL_PATH + "/meld_value.h5")

    train_test = train_test_data('meld_value.csv', 70, 11, 0.01)
    x_test = train_test['x_test']
    y_test = train_test['y_test']

    results = meld_value.evaluate(x_test, y_test, batch_size=16)
    evaToFile(results, 'meld_value')

    predictions = meld_value.predict(x_test[::4])
    truth = y_test[::4]
    savePredTruth(predictions, truth, "meld_value.csv", 11)

####################################################
#---------------------MELD_WILD--------------------#
####################################################


def buildMeldWild():
    meld_wild_input = Input(shape=(70,))
    x = Dense(64, activation='relu')(meld_wild_input)
    x = Dense(32, activation='relu')(x)
    x = Dense(16, activation='relu')(x)
    meld_wild_output = Dense(5, activation='softmax')(x)
    return Model(inputs=meld_wild_input, outputs=meld_wild_output)


def trainMeldWild():
    print("training meld_wild")

    train_test = train_test_data('meld_wild.csv', 70, 5, 0.01)
    x_train = train_test['x_train']
    y_train = train_test['y_train']

    meld_wild = buildMeldWild()

    y_integer = np.argmax(y_train, axis=1)

    class_weight = compute_class_weight(
        'balanced', np.unique(y_integer), y_integer)
    d_class_weight = dict(enumerate(class_weight))

    meld_wild.compile(optimizer=SGD(lr=0.01),
                      loss='categorical_crossentropy',
                      metrics=['accuracy'])
    meld_wild.fit(x_train, y_train, epochs=200,
                  batch_size=64, class_weight=d_class_weight)

    meld_wild.save(MODEL_PATH + "/meld_wild.h5")
    return meld_wild


def evaluateMeldWild(do_training):
    meld_wild = None
    if do_training:
        meld_wild = trainMeldWild()
    else:
        meld_wild = load_model(MODEL_PATH + "/meld_wild.h5")

    train_test = train_test_data('meld_wild.csv', 70, 5, 0.01)
    x_test = train_test['x_test']
    y_test = train_test['y_test']

    results = meld_wild.evaluate(x_test, y_test, batch_size=16)
    evaToFile(results, 'meld_wild')

    predictions = meld_wild.predict(x_test[::2])
    truth = y_test[::2]
    savePredTruth(predictions, truth, "meld_wild.csv", 5)

####################################################
#----------------------ADD_IF----------------------#
####################################################


def buildAddIf():
    add_if_input = Input(shape=(70,))
    x = Dense(64, activation='relu')(add_if_input)
    x = Dense(32, activation='relu')(x)
    x = Dense(16, activation='relu')(x)
    add_if_output = Dense(1, activation='sigmoid')(x)
    return Model(inputs=add_if_input, outputs=add_if_output)


def trainAddIf():
    print("training add_if")

    train_test = train_test_data('add_if.csv', 70, 1, 0.005)
    x_train = train_test['x_train']
    y_train = train_test['y_train']

    add_if = buildAddIf()

    class_weight = compute_class_weight(
        'balanced', np.unique(y_train.flatten()), y_train.flatten())
    d_class_weight = dict(enumerate(class_weight))

    add_if.compile(optimizer=SGD(lr=0.01),
                   loss='binary_crossentropy',
                   metrics=['accuracy'])
    add_if.fit(x_train, y_train, epochs=150,
               batch_size=256, class_weight=d_class_weight)

    add_if.save(MODEL_PATH + "/add_if.h5")
    return add_if


def evaluateAddIf(do_training):
    add_if = None
    if do_training:
        add_if = trainAddIf()
    else:
        add_if = load_model(MODEL_PATH + "/add_if.h5")

    train_test = train_test_data('add_if.csv', 70, 1, 0.005)
    x_test = train_test['x_test']
    y_test = train_test['y_test']

    results = add_if.evaluate(x_test, y_test, batch_size=16)
    evaToFile(results, 'add_if')

    predictions = add_if.predict(x_test[::4])
    truth = y_test[::4]
    savePredTruth(predictions, truth, "add_if.csv", 1)

####################################################
#---------------------ADD_VALUE--------------------#
####################################################


def buildAddValue():
    add_value_input = Input(shape=(70,))
    x = Dense(64, activation='relu')(add_value_input)
    x = Dense(32, activation='relu')(x)
    x = Dense(16, activation='relu')(x)
    add_value_output = Dense(11, activation='softmax')(x)
    return Model(inputs=add_value_input, outputs=add_value_output)


def trainAddValue():
    print("training add_value")

    train_test = train_test_data('add_value.csv', 70, 11, 0.01)
    x_train = train_test['x_train']
    y_train = train_test['y_train']

    add_value = buildAddValue()

    add_value.compile(optimizer=SGD(lr=0.01),
                      loss='categorical_crossentropy',
                      metrics=['accuracy'])
    add_value.fit(x_train, y_train, epochs=150, batch_size=128)

    add_value.save(MODEL_PATH + "/add_value.h5")
    return add_value


def evaluateAddValue(do_training):
    add_value = None
    if do_training:
        add_value = trainAddValue()
    else:
        add_value = load_model(MODEL_PATH + "/add_value.h5")

    train_test = train_test_data('add_value.csv', 70, 11, 0.01)
    x_test = train_test['x_test']
    y_test = train_test['y_test']

    results = add_value.evaluate(x_test, y_test, batch_size=16)
    evaToFile(results, 'add_value')

    predictions = add_value.predict(x_test[::4])
    truth = y_test[::4]
    savePredTruth(predictions, truth, "add_value.csv", 11)

####################################################
#---------------------ADD_WILD---------------------#
####################################################


def buildAddWild():
    add_wild_input = Input(shape=(70,))
    x = Dense(64, activation='relu')(add_wild_input)
    x = Dense(32, activation='relu')(x)
    x = Dense(16, activation='relu')(x)
    add_wild_output = Dense(5, activation='softmax')(x)
    return Model(inputs=add_wild_input, outputs=add_wild_output)


def trainAddWild():
    print("training add_wild")

    train_test = train_test_data('add_wild.csv', 70, 5, 0.01)
    x_train = train_test['x_train']
    y_train = train_test['y_train']

    add_wild = buildAddWild()

    y_integer = np.argmax(y_train, axis=1)

    class_weight = compute_class_weight(
        'balanced', np.unique(y_integer), y_integer)
    d_class_weight = dict(enumerate(class_weight))

    add_wild.compile(optimizer=SGD(lr=0.01),
                     loss='categorical_crossentropy',
                     metrics=['accuracy'])
    add_wild.fit(x_train, y_train, epochs=200,
                 batch_size=64, class_weight=d_class_weight)

    add_wild.save(MODEL_PATH + "/add_wild.h5")
    return add_wild


def evaluateAddWild(do_training):
    add_wild = None
    if do_training:
        add_wild = trainAddWild()
    else:
        add_wild = load_model(MODEL_PATH + "/add_wild.h5")

    train_test = train_test_data('add_wild.csv', 70, 5, 0.01)
    x_test = train_test['x_test']
    y_test = train_test['y_test']

    results = add_wild.evaluate(x_test, y_test, batch_size=16)
    evaToFile(results, 'add_wild')

    predictions = add_wild.predict(x_test[::4])
    truth = y_test[::4]
    savePredTruth(predictions, truth, "add_wild.csv", 5)

####################################################
#-----------------------DROP-----------------------#
####################################################


def buildDrop():
    drop_input = Input(shape=(70,))
    x = Dense(64, activation='relu')(drop_input)
    x = Dense(32, activation='relu')(x)
    x = Dense(16, activation='relu')(x)
    drop_output = Dense(13, activation='softmax')(x)
    return Model(inputs=drop_input, outputs=drop_output)


def trainDrop():
    print("training drop")

    train_test = train_test_data('drop.csv', 70, 13, 0.005)
    x_train = train_test['x_train']
    y_train = train_test['y_train']

    drop = buildDrop()

    drop.compile(optimizer=SGD(lr=0.005),
                 loss='categorical_crossentropy',
                 metrics=['accuracy'])
    drop.fit(x_train, y_train, epochs=550, batch_size=128)

    drop.save(MODEL_PATH + "/drop.h5")
    return drop


def evaluateDrop(do_training):
    drop = None
    if do_training:
        drop = trainDrop()
    else:
        drop = load_model(MODEL_PATH + "/drop.h5")

    train_test = train_test_data('drop.csv', 70, 13, 0.01)
    x_test = train_test['x_test']
    y_test = train_test['y_test']

    results = drop.evaluate(x_test, y_test, batch_size=16)
    evaToFile(results, 'drop')

    predictions = drop.predict(x_test[::4])
    truth = y_test[::4]
    savePredTruth(predictions, truth, "drop.csv", 13)

####################################################
#-----------------------HELP-----------------------#
####################################################


def fileLen(fname):
    with open(fname) as f:
        for i, l in enumerate(f):
            pass
    return i + 1


def train_test_data(file_name, input_dim, output_dim, test_size):
    draw_count = fileLen(DATA_PATH + '/input/' + file_name)

    x = np.zeros(shape=(draw_count, input_dim))
    y = np.zeros(shape=(draw_count, output_dim))

    i = 0
    with open(DATA_PATH + '/input/' + file_name, 'r') as f:
        for l in f:
            x[i] = np.fromstring(l, sep=',')
            i += 1
    i = 0
    with open(DATA_PATH + '/output/' + file_name, 'r') as f:
        for l in f:
            y[i] = np.fromstring(l, sep=',')
            i += 1

    x_train, x_test, y_train, y_test = train_test_split(
        x, y, test_size=test_size, random_state=42)

    return {'x_train': x_train, 'x_test': x_test, 'y_train': y_train, 'y_test': y_test}


def savePredTruth(predictions, truth, fname, out_dim):
    pred_truth = None
    if out_dim != 1:
        length = int(2 * truth.size / out_dim)
        pred_truth = np.zeros(shape=(length, out_dim))
        x = 0
        y = 0
        for i in range(0, length):
            if i % 2 == 0:
                pred_truth[i] = truth[x]
                x += 1
            else:
                pred_truth[i] = predictions[y]
                y += 1
    else:
        pred_truth = np.zeros(shape=(2, truth.size))
        for i in range(0, truth.size):
            pred_truth[0][i] = truth[i][0]
            pred_truth[1][i] = predictions[i][0]

    np.savetxt(DATA_PATH + '/predictions/' + fname,
               pred_truth, delimiter=',', fmt='%1.2f')


def evaToFile(eva, name):
    open(MODEL_PATH + '/eva/' + name + '.txt', 'w').close()
    f = open(MODEL_PATH + '/eva/' + name + '.txt', 'a')
    f.write(name + ' test loss, test acc: ' + str(eva))
    f.close()


def evalAll(do_training):
    evaluateDraw(do_training)
    evaluateMeldIf(do_training)
    evaluateAddIf(do_training)
    evaluateMeldValue(do_training)
    evaluateAddValue(do_training)
    evaluateMeldWild(do_training)
    evaluateAddWild(do_training)
    evaluateDrop(do_training)


def main():
    np.set_printoptions(formatter={'float': lambda x: "{0:0.1f}".format(x)})

    evalAll(True)
    print("Done")


if __name__ == "__main__":
    main()
