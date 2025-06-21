//20211080_20211093_20210329_20211075_S5,6
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (
                BufferedReader br = new BufferedReader(new FileReader("input_pda.txt"));
                BufferedWriter bw = new BufferedWriter(new FileWriter("output_pda.txt"))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int problemNumber = Integer.parseInt(line);
                bw.write(problemNumber + "\n");
                StringWriter sw = new StringWriter();
                BufferedWriter tempBw = new BufferedWriter(sw);
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null && !line.trim().equals("end")) {
                    sb.append(line).append("\n");
                }
                BufferedReader problemReader = new BufferedReader(new StringReader(sb.toString()));
                switch (problemNumber) {
                    case 1 -> {
                        Problem1 problem1 = new Problem1(problemReader, tempBw);
                    }
                    case 2 -> {
                        Problem2 problem2 = new Problem2(problemReader, tempBw);
                    }
                    case 3 -> {
                        Problem3 problem3 = new Problem3(problemReader, tempBw);
                    }
                    case 4 -> {
                        Problem4 problem4 = new Problem4(problemReader, tempBw);
                    }
                    case 5 ->{
                        Problem5 problem5 = new Problem5(problemReader,tempBw);
                    }
                    default -> {
                        tempBw.write("Invalid problem number\n");
                        tempBw.flush();
                    }
                }
                tempBw.flush();
                bw.write(sw.toString());
                bw.write("x\n");
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class PDAClass {
    private final ArrayList<Integer> states;
    private final ArrayList<Character> inputAlphabet;
    private final ArrayList<Character> stackAlphabet;
    private final TransitionFunction transitionFunction;
    private final int startState;
    private final ArrayList<Integer> finalStates;
    private final char stackInitial;
    PDAClass(ArrayList<Integer> states, ArrayList<Character> inputAlphabet,
             ArrayList<Character> stackAlphabet, TransitionFunction transitionFunction,
             int startState, ArrayList<Integer> finalStates, char stackInitial) {
        this.states = states;
        this.inputAlphabet = inputAlphabet;
        this.stackAlphabet = stackAlphabet;
        this.transitionFunction = transitionFunction;
        this.startState = startState;
        this.finalStates = finalStates;
        this.stackInitial = stackInitial;
    }
    public boolean isAccepted(String input) {
        Stack<Character> stack = new Stack<>();
        return dfs(startState, input, 0, stack);
    }
    private boolean dfs(int currentState, String input, int index, Stack<Character> stack) {
        // Base case: if we reach the end of the input
        if (index == input.length() && finalStates.contains(currentState) && stack.size() == 0) {
            return true;
        }
        char currentInput = index < input.length() ? input.charAt(index) : 'ε';
        if(!inputAlphabet.contains(currentInput) && currentInput != 'ε') {
            return false;
        }
        if(!states.contains(currentState)) {
            return false;
        }
        char stackTop = stack.isEmpty() ? 'ε' : stack.peek();
        TransitionValue normalTransition = transitionFunction.getTransition(currentState, currentInput, stackTop);
        if (normalTransition != null) {
            if (!stack.isEmpty())
            {
                stack.pop();
            }
            for (int i = normalTransition.stackPush.length() - 1; i >= 0; i--) {
                char c = normalTransition.stackPush.charAt(i);
                if (c != 'ε' &&stackAlphabet.contains(c))
                {
                    stack.push(c);
                }
            }
            if (dfs(normalTransition.nextState, input, index + 1, stack)) return true;
        }
        TransitionValue epsilonTransition = transitionFunction.getTransition(currentState, 'ε', stackTop);
        if (epsilonTransition != null) {
            if (!stack.isEmpty())
            {
                stack.pop();
            }
            for (int i = epsilonTransition.stackPush.length() - 1; i >= 0; i--) {
                char c = epsilonTransition.stackPush.charAt(i);
                if (c != 'ε' && stackAlphabet.contains(c))
                {
                    stack.push(c);
                }
            }
            if (dfs(epsilonTransition.nextState, input, index, stack)) return true;
        }
        return false;
    }
    public void solveProblem(BufferedReader br, BufferedWriter bw) {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                boolean result = isAccepted(line);
                bw.write(result ? "accepted" : "not accepted");
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class TransitionKey {
    int currentState;
    char input;
    char stackTop;

    TransitionKey(int currentState, char input, char stackTop) {
        this.currentState = currentState;
        this.input = input;
        this.stackTop = stackTop;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransitionKey tk)) return false;
        return currentState == tk.currentState && input == tk.input && stackTop == tk.stackTop;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentState, input, stackTop);
    }
}
class TransitionValue {
    int nextState;
    String stackPush;

    TransitionValue(int nextState, String stackPush) {
        this.nextState = nextState;
        this.stackPush = stackPush;
    }
}
class TransitionFunction {
    public final Map<TransitionKey, TransitionValue> transitions;
    TransitionFunction() {
        transitions = new HashMap<>();
    }
    public void addTransition(int currentState, char input, char stackTop, int nextState, String stackPush) {
        transitions.put(new TransitionKey(currentState, input, stackTop), new TransitionValue(nextState, stackPush));
    }
    public TransitionValue getTransition(int currentState, char input, char stackTop) {
        return transitions.get(new TransitionKey(currentState, input, stackTop));
    }
}
class Problem1 {
    PDAClass pda;
    public Problem1(BufferedReader br, BufferedWriter bw) {
        ArrayList<Integer> states = new ArrayList<>(Arrays.asList(0, 1, 2,3));
        ArrayList<Integer> finalStates = new ArrayList<>(List.of(3));
        ArrayList<Character> inputAlphabet = new ArrayList<>(Arrays.asList('a', 'b', 'c'));
        ArrayList<Character> stackAlphabet = new ArrayList<>(Arrays.asList('a', '$'));
        int startState = 0;
        char stackInitial = '$';
        TransitionFunction tf = new TransitionFunction();
        tf.addTransition(0, 'ε', 'ε', 1, String.valueOf(stackInitial));
        tf.addTransition(1, 'a', '$', 1, "a$");
        tf.addTransition(1, 'a', 'a', 1, "aa");
        tf.addTransition(1, 'b', 'a', 1, "a");
        tf.addTransition(1, 'b', '$', 1, "$");
        tf.addTransition(1, 'c', 'a', 2, "");
        tf.addTransition(2, 'c', 'a', 2, "");
        tf.addTransition(2, 'ε', '$', 3, "");
        tf.addTransition(1, 'ε', '$', 3, "");
        pda = new PDAClass(states, inputAlphabet, stackAlphabet, tf, startState, finalStates, stackInitial);
        pda.solveProblem(br, bw);
    }
}
 class Problem2 {
    PDAClass pda;

    public Problem2(BufferedReader br, BufferedWriter bw) {
        ArrayList<Integer> states = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4,5));
        ArrayList<Integer> finalStates = new ArrayList<>(List.of(5));
        ArrayList<Character> inputAlphabet = new ArrayList<>(Arrays.asList('a', 'b'));
        ArrayList<Character> stackAlphabet = new ArrayList<>(Arrays.asList('$', 'a'));
        int startState = 0;
        char stackInitial = '$';
        TransitionFunction tf = new TransitionFunction();
        tf.addTransition(0, 'ε', 'ε', 1, String.valueOf(stackInitial));
        tf.addTransition(1, 'a', '$', 2, "$");
        tf.addTransition(1, 'a', 'a', 2, "a");
        tf.addTransition(2, 'a', '$', 3, "$");
        tf.addTransition(2, 'a', 'a', 3, "a");
        tf.addTransition(3, 'a', '$', 1, "aa$");
        tf.addTransition(3, 'a', 'a', 1, "aaa");
        tf.addTransition(1, 'b', 'a', 4, "");
        tf.addTransition(4, 'b', 'a', 4, "");
        tf.addTransition(4, 'ε', '$', 5, "");
        pda = new PDAClass(states, inputAlphabet, stackAlphabet, tf, startState, finalStates, stackInitial);
        pda.solveProblem(br, bw);
    }
}
class Problem3 {
    PDAClass pda;
    public Problem3(BufferedReader br, BufferedWriter bw) {
        ArrayList<Integer> states = new ArrayList<>(Arrays.asList(0, 1,2));
        ArrayList<Integer> finalStates = new ArrayList<>(List.of(2));
        ArrayList<Character> inputAlphabet = new ArrayList<>(Arrays.asList('{', '}'));
        ArrayList<Character> stackAlphabet = new ArrayList<>(Arrays.asList('{', '$'));
        int startState = 0;
        char stackInitial = '$';
        TransitionFunction tf = new TransitionFunction();
        tf.addTransition(0, 'ε', 'ε', 1, String.valueOf(stackInitial));
        tf.addTransition(1, '{', '$', 1, "{$");
        tf.addTransition(1, '{', '{', 1, "{{");
        tf.addTransition(1, '}', '{', 1, "");
        tf.addTransition(1, 'ε', '$', 2, "");
        pda = new PDAClass(states, inputAlphabet, stackAlphabet, tf, startState, finalStates, stackInitial);
        pda.solveProblem(br, bw);
    }
}
class Problem4 {
    PDAClass pda;
    public Problem4(BufferedReader br, BufferedWriter bw) {
        ArrayList<Integer> states = new ArrayList<>(Arrays.asList(0, 1, 2, 3,4));
        ArrayList<Integer> finalStates = new ArrayList<>(List.of(4));
        ArrayList<Character> inputAlphabet = new ArrayList<>(Arrays.asList('a', 'b', 'c'));
        ArrayList<Character> stackAlphabet = new ArrayList<>(Arrays.asList('a', 'b', '$'));
        int startState = 0;
        char stackInitial = '$';
        TransitionFunction tf = new TransitionFunction();
        tf.addTransition(0, 'ε', 'ε', 1, String.valueOf(stackInitial));
        tf.addTransition(1, 'a', '$', 1, "a$");
        tf.addTransition(1, 'a', 'a', 1, "aa");
        tf.addTransition(1, 'b', 'a', 2, "");
        tf.addTransition(2, 'b', 'a', 2, "");
        tf.addTransition(2, 'b', '$', 2, "b$");
        tf.addTransition(2, 'b', 'b', 2, "bb");
        tf.addTransition(2, 'c', 'b', 3, "");
        tf.addTransition(3, 'c', 'b', 3, "");
        tf.addTransition(3, 'ε', '$', 4, "");
        pda = new PDAClass(states, inputAlphabet, stackAlphabet, tf, startState, finalStates, stackInitial);
        pda.solveProblem(br, bw);
    }
}
class Problem5 {
    PDAClass pda;
    public Problem5(BufferedReader br, BufferedWriter bw) {
        ArrayList<Integer> states = new ArrayList<>(Arrays.asList(0, 1, 2,3));
        ArrayList<Integer> finalStates = new ArrayList<>(List.of(3));
        ArrayList<Character> inputAlphabet = new ArrayList<>(Arrays.asList('a', 'b', 'c'));
        ArrayList<Character> stackAlphabet = new ArrayList<>(Arrays.asList('b', '$'));
        int startState = 0;
        char stackInitial = '$';
        TransitionFunction tf = new TransitionFunction();
        tf.addTransition(0, 'ε', 'ε', 1, String.valueOf(stackInitial));
        tf.addTransition(1, 'a', '$', 1, "$");
        tf.addTransition(1, 'a', 'b', 1, "b");
        tf.addTransition(1, 'b', '$', 1, "b$");
        tf.addTransition(1, 'b', 'b', 1, "bb");
        tf.addTransition(1, 'c', 'b', 2, "");
        tf.addTransition(2, 'c', 'b', 2, "");
        tf.addTransition(2, 'ε', '$', 3, "");
        pda = new PDAClass(states, inputAlphabet, stackAlphabet, tf, startState, finalStates, stackInitial);
        pda.solveProblem(br, bw);
    }
}
