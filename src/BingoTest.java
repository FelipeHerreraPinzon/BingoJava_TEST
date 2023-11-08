import java.util.Random;
import javax.swing.JOptionPane;

public class BingoTest {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";

    public static void main(String[] args) {
        int cantidadCartones = solicitarCantidadCartones();
        String[] nombresCartones = new String[cantidadCartones];

        int[][] cartones = new int[cantidadCartones][25];
        boolean[] cartonLleno = new boolean[cantidadCartones];

        for (int i = 0; i < cantidadCartones; i++) {
            nombresCartones[i] = solicitarNombreCarton(i + 1);
            generarCarton(cartones[i]);
            cartonLleno[i] = false;
        }

        imprimirCartones(cartones, nombresCartones);
        presionarEnter();

        Random random = new Random();
        String[] letras = {"B", "I", "N", "G", "O"};
        boolean juegoTerminado = false;

        while (!juegoTerminado) {
            String letra = letras[random.nextInt(5)];
            int numero = random.nextInt(75) + 1;

            System.out.println("\nNúmero llamado: " + letra + " " + numero + "\n");

            for (int i = 0; i < cantidadCartones; i++) {
                if (!cartonLleno[i] && marcarCarton(cartones[i], letra, numero)) {
                    cartonLleno[i] = verificarBingo(cartones[i]);
                    imprimirCartonConX(cartones[i], nombresCartones[i]);
                    if (cartonLleno[i]) {
                        JOptionPane.showMessageDialog(null, "¡BINGO en el " + nombresCartones[i] + "!");
                    }
                }
            }

            juegoTerminado = verificarJuegoTerminado(cartonLleno);
            if (!juegoTerminado) {
                presionarEnter();
            }
        }
    }

    private static int solicitarCantidadCartones() {
        int cantidadCartones = 0;
        while (cantidadCartones < 1 || cantidadCartones > 12) {
            String input = JOptionPane.showInputDialog("Ingrese la cantidad de cartones (1-12):");
            try {
                cantidadCartones = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // En caso de que no se ingrese un número válido.

            }
        }
        return cantidadCartones;
    }

    private static String solicitarNombreCarton(int numeroCarton) {
        return JOptionPane.showInputDialog("Nombre para el cartón " + numeroCarton + " (máximo 15 caracteres):");
    }

    private static void generarCarton(int[] carton) {
        Random random = new Random();
        int inicio = 1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (j == 2 && i == 2) {
                    carton[i * 5 + j] = 0; // El espacio central es un espacio libre
                } else {
                    carton[i * 5 + j] = random.nextInt(15) + inicio;
                }
            }
            inicio += 15;
        }
    }

    private static void imprimirCartones(int[][] cartones, String[] nombresCartones) {
        for (int i = 0; i < cartones.length; i++) {
            System.out.println(nombresCartones[i] + ":");
            imprimirCarton(cartones[i]);
        }
    }

    private static void imprimirCarton(int[] carton) {
        String[] columnas = {"B", "I", "N", "G", "O"};
        int currentIndex = 0;

        for (int i = 0; i < columnas.length; i++) {
            System.out.print(columnas[i] + "\t");
        }
        System.out.println();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < columnas.length; j++) {
                int numero = carton[currentIndex];
                if (numero == 0) {
                    System.out.print(ANSI_GREEN_BACKGROUND + "X" + ANSI_RESET + "\t"); // Fondo verde
                } else {
                    System.out.print(numero + "\t");
                }
                currentIndex++;
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void imprimirCartonConX(int[] carton, String nombreCarton) {
        System.out.println(nombreCarton + ":");
        imprimirCarton(carton);
    }

    private static boolean marcarCarton(int[] carton, String letra, int numero) {
        for (int i = 0; i < 25; i++) {
            if (carton[i] == numero && letra.equals(getColumnaLetra(i))) {
                carton[i] = 0; // Marcar con "X"
                return true;
            }
        }
        return false;
    }

    private static String getColumnaLetra(int index) {
        String[] columnas = {"B", "I", "N", "G", "O"};
        return columnas[index % 5];
    }

    private static boolean verificarBingo(int[] carton) {
        // Implementa la lógica para verificar un BINGO en filas, columnas y diagonales
        // Retorna true si se encuentra un BINGO, de lo contrario, retorna false.
        return false;
    }

    private static boolean verificarJuegoTerminado(boolean[] cartonesLlenos) {
        for (boolean cartonLleno : cartonesLlenos) {
            if (!cartonLleno) {
                return false;
            }
        }
        return true;
    }

    private static void presionarEnter() {
        System.out.println("Presiona Enter para continuar...");
        new java.util.Scanner(System.in).nextLine();
    }
}
