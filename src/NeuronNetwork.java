import java.util.ArrayList;

class NeuronNetwork {
    private final int CountOfInputs;
    private final int[] CountOfNeuronsInHiddenLayer;
    private final int CountOfOutputs;

    private InputLayer input;
    private HiddenLayer[] hidden;
    private OutputLayer output;

    NeuronNetwork(int countOfInputs, int[] countOfNeuronsInHiddenLayer, int countOfOutputs) {
        CountOfInputs = countOfInputs;
        CountOfNeuronsInHiddenLayer = countOfNeuronsInHiddenLayer;
        CountOfOutputs = countOfOutputs;
        hidden = new HiddenLayer[countOfNeuronsInHiddenLayer.length];
    }

    void init() {
        input = new InputLayer(CountOfInputs);
        for (int i = 0; i < hidden.length; i++) {
            hidden[i] = new HiddenLayer(CountOfNeuronsInHiddenLayer[i]);
        }
        output = new OutputLayer(CountOfOutputs);

        input.init(hidden[0]);
        for (int i = 0; i < hidden.length; i++) {
            hidden[i].init(i - 1 < 0 ? input : hidden[i - 1], i + 1 >= hidden.length ? output : hidden[i + 1]);
        }
        output.init(hidden[hidden.length - 1]);

        if (FileManager.exist(Main.NWFile)) {
            if (loadData())
                System.err.println("Load Data was successfully");
        } else {
            generateRandomWeigths();
        }
    }

    int step(double[] input) {
        if (input.length != CountOfInputs) {
            System.err.println("Error in step()");
            System.exit(-142);
        }
        for (int i = 0; i < this.input.neurons.length; i++) {
            this.input.neurons[i].output = /*Neuron.activationFunction(*/input[i];
        }
        this.input.next.update();
        return getMaxOutput();
    }

    void adjustment(double[] ideal) {
        //printResultStep(ideal);

        if (ideal.length != output.neurons.length) {
            System.err.println("Error in adjustment");
            System.exit(-143);
        }
        for (int i = 0; i < output.neurons.length; i++) {
            output.neurons[i].delta = (ideal[i] - output.neurons[i].output) * ((1 - output.neurons[i].output) * output.neurons[i].output);
        }
        output.previous.adjustment();
    }

    void saveData() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(status());
        stringBuilder.append('\n');
        stringBuilder.append(stringBuilder.toString().hashCode());

        FileManager.erase(Main.NWFile);
        FileManager.write(Main.NWFile, stringBuilder.toString());
        System.err.println("Save Data was successfully");
    }

    private void printResultStep(double[] ideal) {
        double[] result = new double[output.neurons.length];
        if (result.length != ideal.length) {
            System.exit(4433);
        }

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < output.neurons.length; i++) {
            result[i] = output.neurons[i].output;
            s.append(result[i]).append("\n");
        }
        double err = 0;

        for (int i = 0; i < result.length; i++) {
            err += Math.pow(ideal[i] - result[i], 2);
        }
        s.append((int) Math.round(err * 100 / result.length)).append("\n");
        System.out.println(s.toString());
    }

    private String status() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(CountOfInputs);
        stringBuilder.append(":");
        for (int i = 0; i < CountOfNeuronsInHiddenLayer.length - 1; i++) {
            stringBuilder.append(CountOfNeuronsInHiddenLayer[i]);
            stringBuilder.append(",");
        }
        stringBuilder.append(CountOfNeuronsInHiddenLayer[CountOfNeuronsInHiddenLayer.length - 1]);
        stringBuilder.append(":");
        stringBuilder.append(CountOfOutputs);

        stringBuilder.append('\n');

        for (HiddenLayer aHidden : hidden) {
            for (int j = 0; j < aHidden.neurons.length - 1; j++) {
                for (int k = 0; k < aHidden.neurons[j].weight.length - 1; k++) {
                    stringBuilder.append(aHidden.neurons[j].weight[k]);
                    stringBuilder.append(",");
                }
                stringBuilder.append(aHidden.neurons[j].weight[aHidden.neurons[j].weight.length - 1]);
                stringBuilder.append(":");
            }
            for (int k = 0; k < aHidden.neurons[aHidden.neurons.length - 1].weight.length - 1; k++) {
                stringBuilder.append(aHidden.neurons[aHidden.neurons.length - 1].weight[k]);
                stringBuilder.append(",");
            }
            stringBuilder.append(aHidden.neurons[aHidden.neurons.length - 1].weight[aHidden.neurons[aHidden.neurons.length - 1].weight.length - 1]);
            stringBuilder.append('\n');
        }
        for (int i = 0; i < output.neurons.length - 1; i++) {
            for (int j = 0; j < output.neurons[i].weight.length - 1; j++) {
                stringBuilder.append(output.neurons[i].weight[j]);
                stringBuilder.append(",");
            }
            stringBuilder.append(output.neurons[i].weight[output.neurons[i].weight.length - 1]);
            stringBuilder.append(":");
        }
        for (int j = 0; j < output.neurons[output.neurons.length - 1].weight.length - 1; j++) {
            stringBuilder.append(output.neurons[output.neurons.length - 1].weight[j]);
            stringBuilder.append(",");
        }
        stringBuilder.append(output.neurons[output.neurons.length - 1].weight[output.neurons[output.neurons.length - 1].weight.length - 1]);
        return stringBuilder.toString();
    }

    private boolean loadData() {
        ArrayList<String> archive = FileManager.read(Main.NWFile);
        if (archive == null || archive.isEmpty()) return false;
        if (check(archive)) {
            for (int i = 0; i < hidden.length; i++) {
                String[] hiddenLayer = archive.get(i + 1).split(":");
                for (int j = 0; j < hidden[i].neurons.length; j++) {
                    String[] weights = hiddenLayer[j].split(",");
                    for (int k = 0; k < hidden[i].neurons[j].weight.length; k++) {
                        hidden[i].neurons[j].weight[k] = Double.parseDouble(weights[k]);
                    }
                }

            }
            String[] outs = archive.get(archive.size() - 2).split(":");
            for (int i = 0; i < output.neurons.length; i++) {
                String[] weights = outs[i].split(",");
                for (int j = 0; j < output.neurons[i].weight.length; j++) {
                    output.neurons[i].weight[j] = Double.parseDouble(weights[j]);
                }
            }
            return true;
        } else {
            System.err.println("Error in loadData");
            return false;
        }
    }

    private boolean check(ArrayList<String> archive) {
        String[] parameters = archive.get(0).split(":");
        String[] hiddenS = parameters[1].split(",");

        if (input.neurons.length == Integer.valueOf(parameters[0])) {
            if (output.neurons.length == Integer.valueOf(parameters[2])) {
                if (hidden.length == hiddenS.length) {
                    for (int i = 0; i < hidden.length; i++) {
                        if (hidden[i].neurons.length != Integer.valueOf(hiddenS[i])) {
                            return false;
                        }
                    }
                } else return false;
            } else return false;
        } else return false;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < archive.size() - 2; i++) {
            stringBuilder.append(archive.get(i));
            stringBuilder.append('\n');
        }
        stringBuilder.append(archive.get(archive.size() - 2));
        stringBuilder.append('\n');
        return stringBuilder.toString().hashCode() == Integer.parseInt(archive.get(archive.size() - 1));
    }

    private void generateRandomWeigths() {
        for (HiddenLayer aHidden : hidden) {
            for (int j = 0; j < aHidden.neurons.length; j++) {
                for (int k = 0; k < aHidden.neurons[j].weight.length; k++) {
                    double rand = Math.random();
                    aHidden.neurons[j].weight[k] = rand > 0.5 ? rand - 1 : rand;
                }
            }
        }
        for (int i = 0; i < output.neurons.length; i++) {
            for (int j = 0; j < output.neurons[i].weight.length; j++) {
                double rand = Math.random();
                output.neurons[i].weight[j] = rand > 0.5 ? rand - 1 : rand;
            }
        }
    }

    private int getMaxOutput() {
        /*int i=-2;
        double d = Double.MIN_VALUE;

        for (int i1 = 0; i1 < output.neurons.length; i1++) {
            if (output.neurons[i1].output > d) {
                d = output.neurons[i1].output;
                i = i1;
            }
        }
        i--;*/
        double max = Double.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < output.neurons.length; i++) {
            if (output.neurons[i].output > max) {
                max = output.neurons[i].output;
                index = i;
            }
        }
        return index;

        //
        /*int i=-2;

        if (output.neurons[0].output>=0&&output.neurons[0].output<1d/3) {
            i=-1;
        } else if (output.neurons[0].output>=1d/3&&output.neurons[0].output<2d/3) {
            i=0;
        } else if (output.neurons[0].output>=2d/3&&output.neurons[0].output<=1) {
            i=1;
        }

        if (i<-1||i>1) {
            System.exit(999);
        }
        //

        return i;*/
    }
}

abstract class Layer {
    Layer previous;
    Layer next;
    Neuron[] neurons;

    Layer(int neurons) {
        this.neurons = new Neuron[neurons];
    }

    void update() {
        for (Neuron neuron : neurons) {
            for (int j = 0; j < previous.neurons.length; j++) {
                neuron.input[j] = previous.neurons[j].output;
            }
            neuron.calculate();
        }
        if (next != null) {
            next.update();
        }
    }

    void adjustment() {
        for (int i = 0; i < neurons.length; i++) {
            for (int j = 0; j < next.neurons.length; j++) {
                neurons[i].delta += next.neurons[j].weight[i] * next.neurons[j].delta;
            }
            neurons[i].delta *= ((1 - neurons[i].output) * neurons[i].output);
        }
        for (int i = 0; i < next.neurons.length; i++) {
            for (int j = 0; j < neurons.length; j++) {
                next.neurons[i].moment[j] = Main.Epsilon * neurons[j].output * next.neurons[i].delta + Main.Alpha * next.neurons[i].moment[j];
                next.neurons[i].weight[j] += next.neurons[i].moment[j];
            }
        }
        previous.adjustment();
    }
}

class InputLayer extends Layer {
    InputLayer(int neurons) {
        super(neurons);
    }

    void init(Layer next) {
        super.next = next;
        for (int i = 0; i < neurons.length; i++) {
            neurons[i] = new Neuron(0);
        }
    }

    @Override
    void adjustment() {
        for (int i = 0; i < next.neurons.length; i++) {
            for (int j = 0; j < neurons.length; j++) {
                next.neurons[i].moment[j] = Main.Epsilon * neurons[j].output * next.neurons[i].delta + Main.Alpha * next.neurons[i].moment[j];
                next.neurons[i].weight[j] += next.neurons[i].moment[j];
            }
        }
    }
}

class HiddenLayer extends Layer {
    HiddenLayer(int neurons) {
        super(neurons);
    }

    void init(Layer previous, Layer next) {
        super.previous = previous;
        super.next = next;
        for (int i = 0; i < neurons.length; i++) {
            neurons[i] = new Neuron(previous.neurons.length);
        }
    }
}

class OutputLayer extends Layer {
    OutputLayer(int neurons) {
        super(neurons);
    }

    void init(Layer previous) {
        super.previous = previous;
        for (int i = 0; i < neurons.length; i++) {
            neurons[i] = new Neuron(previous.neurons.length);
        }
    }
}

class Neuron {
    double[] input;
    double[] weight;
    double[] moment;
    double delta = 0;
    double output = 0;

    Neuron(int inputs) {
        if (inputs > 0) {
            input = new double[inputs];
            weight = new double[inputs];
            moment = new double[inputs];
        }
    }

    void calculate() {
        for (int i = 0; i < input.length; i++) {
            output += input[i] * weight[i];
        }
        normalize(output);
    }

    private void normalize(double i) {
        output = activationFunction(i);
    }

    private double activationFunction(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}