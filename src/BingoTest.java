// Importación de librerías necesarias

import java.util.Random;
import javax.swing.JOptionPane;
import java.util.Scanner;

// Definición de la clase principal
public class BingoTest {

    // Constantes para el formato del texto
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";

    // Scanner global para la entrada de datos
    private static final Scanner scanner = new Scanner(System.in);



    // Lista para almacenar el historial de letras y números llamados
    private static String[][] historial;
    // Índice para seguir un registro del historial
    private static int indiceHistorial;

    // Método principal
    public static void main(String[] args) {

        // Declaración de variables
        int cantidadCartones = solicitarCantidadCartones();
        String[] nombresCartones = new String[cantidadCartones];
        int[][] cartones = new int[cantidadCartones][25];
        boolean[] cartonLleno = new boolean[cantidadCartones];

        // Inicialización del historial
        historial = new String[5 * 15][2];
        indiceHistorial = 0;

        // Generación e impresión de los cartones numerados
        for (int i = 0; i < cantidadCartones; i++) {
            nombresCartones[i] = "Cartón " + (i + 1);
            generarCarton(cartones[i]);
            cartonLleno[i] = false;
        }
        imprimirCartones(cartones, nombresCartones);

        // Mensaje y asignación de nombres a los cartones
        asignarCartones();
        for (int i = 0; i < cantidadCartones; i++) {
            nombresCartones[i] = solicitarNombreCarton(i + 1);
        }

        // Generación e impresión de cartones con nombres asignados
        for (int i = 0; i < cantidadCartones; i++) {
            nombresCartones[i] = "Cartón " + (i + 1) + " - " + nombresCartones[i];
            generarCarton(cartones[i]);
            cartonLleno[i] = false;
        }
        imprimirCartones(cartones, nombresCartones);

        // Mensaje de inicio del juego
        iniciarJuego();

        // Inicio del juego
        Random random = new Random();
        String[] letras = {"B", "I", "N", "G", "O"};
        boolean juegoTerminado = false;

        while (!juegoTerminado) {
            String letra = letras[random.nextInt(5)];
            int numero = generarNumeroPorColumna(letra);

            // Verificar si la combinación ya ha sido seleccionada
            while (combinacionRepetida(letra, numero)) {
                letra = letras[random.nextInt(5)];
                numero = generarNumeroPorColumna(letra);
            }

            // Agregar la combinación al historial
            agregarAlHistorial(letra, numero);

            System.out.println("\nNúmero llamado: " + letra + " " + numero + "\n");

            for (int i = 0; i < cantidadCartones; i++) {
                if (!cartonLleno[i] && marcarCarton(cartones[i], letra, numero)) {
                    cartonLleno[i] = verificarBingo(cartones[i]);
                    imprimirCartonConX(cartones[i], nombresCartones[i]);
                    if (cartonLleno[i]) {
                        JOptionPane.showMessageDialog(null, nombresCartones[i] + " hizo BINGO!!!");
                    } else {
                        JOptionPane.showMessageDialog(null, nombresCartones[i] + " coincidió con " + letra + " " + numero);
                    }
                }
            }

            juegoTerminado = verificarJuegoTerminado(cartonLleno);
            if (!juegoTerminado) {
                presionarEnter();
            }
        }
    }

    // Método para verificar si la combinación ya ha sido seleccionada
    private static boolean combinacionRepetida(String letra, int numero) {
        for (int i = 0; i < indiceHistorial; i++) {
            if (letra.equals(historial[i][0]) && numero == Integer.parseInt(historial[i][1])) {
                return true;
            }
        }
        return false;
    }

    // Método para solicitar la cantidad de cartones
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

    // Método para solicitar el nombre de un cartón
    private static String solicitarNombreCarton(int numeroCarton) {
        return JOptionPane.showInputDialog("Nombre para el cartón " + numeroCarton + " (máximo 15 caracteres):");
    }

    private static void generarCarton(int[] carton) {
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (j == 2 && i == 2) {
                    carton[i * 5 + j] = 0; // El espacio central es un espacio libre
                } else {
                    int nuevoNumero;
                    boolean numeroRepetido;

                    do {
                        numeroRepetido = false;
                        nuevoNumero = generarNumeroPorColumna(getColumnaLetra(j));

                        // Verificar si el nuevo número ya existe en la columna actual
                        for (int k = 0; k < i; k++) {
                            if (carton[k * 5 + j] == nuevoNumero) {
                                numeroRepetido = true;
                                break;
                            }
                        }
                    } while (numeroRepetido);

                    carton[i * 5 + j] = nuevoNumero;
                }
            }
        }
    }

    // Método para generar un número según la columna (letra) del cartón
    private static int generarNumeroPorColumna(String letra) {
        Random random = new Random();
        switch (letra) {
            case "B":
                return random.nextInt(15) + 1;
            case "I":
                return random.nextInt(15) + 16;
            case "N":
                return random.nextInt(15) + 31;
            case "G":
                return random.nextInt(15) + 46;
            case "O":
                return random.nextInt(15) + 61;
            default:
                return 0; // En caso de letra desconocida
        }
    }

    // Método para imprimir los cartones
    private static void imprimirCartones(int[][] cartones, String[] nombresCartones) {
        for (int i = 0; i < cartones.length; i++) {
            System.out.println("\u001B[34m" + nombresCartones[i] + ":" + "\u001B[0m"); // Texto azul para los nombres
            imprimirCarton(cartones[i]);
        }
    }

    // Método para imprimir un cartón
    private static void imprimirCarton(int[] carton) {
        String[] columnas = {"\u001B[31mB", "\u001B[31mI", "\u001B[31mN", "\u001B[31mG", "\u001B[31mO"}; // Texto rojo para las letras BINGO
        int currentIndex = 0;

        for (int i = 0; i < columnas.length; i++) {
            System.out.print(columnas[i] + "\t" + "\u001B[0m"); // Restablecer color
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

    // Método para imprimir un cartón con X
    private static void imprimirCartonConX(int[] carton, String nombreCarton) {
        System.out.println("\u001B[34m" + nombreCarton + ":" + "\u001B[0m"); // Texto azul
        imprimirCarton(carton);
    }

    // Método para marcar un número en el cartón
    private static boolean marcarCarton(int[] carton, String letra, int numero) {
        for (int i = 0; i < 25; i++) {
            if (carton[i] == numero && letra.equals(getColumnaLetra(i))) {
                carton[i] = 0; // Marcar con "X"
                return true;
            }
        }
        return false;
    }

    // Método para obtener la letra de una columna en el cartón
    private static String getColumnaLetra(int index) {
        String[] columnas = {"B", "I", "N", "G", "O"};
        return columnas[index % 5];
    }

    // Método para verificar si hay un BINGO en el cartón
    private static boolean verificarBingo(int[] carton) {
        // Implementa la lógica para verificar un BINGO en filas, columnas y diagonales
        // Retorna true si se encuentra un BINGO, de lo contrario, retorna false.
        return false;
    }

    // Método para verificar si el juego ha terminado
    private static boolean verificarJuegoTerminado(boolean[] cartonesLlenos) {
        for (boolean cartonLleno : cartonesLlenos) {
            if (!cartonLleno) {
                return false;
            }
        }
        return true;
    }

    // Método para esperar la presión de Enter
    private static void presionarEnter() {
        System.out.print("\u001B[33mPresiona Enter para sortear...\u001B[0m"); // Texto amarillo
        scanner.nextLine();
    }

    // Método para mostrar mensaje de inicio de juego
    private static void iniciarJuego() {
        System.out.print("\u001B[33mCartones Asignados !!!, pulsa ENTER para empezar\u001B[0m"); // Texto amarillo
        scanner.nextLine();
        scanner.nextLine();
    }

    // Método para mostrar mensaje de asignación de cartones
    private static void asignarCartones() {
        System.out.print("\u001B[33mAhora vamos a asignar los cartones, PRESIONA ENTER...\u001B[0m"); // Texto amarillo
        scanner.nextLine();
    }

    // Método para agregar la combinación al historial
    private static void agregarAlHistorial(String letra, int numero) {
        historial[indiceHistorial][0] = letra;
        historial[indiceHistorial][1] = Integer.toString(numero);
        indiceHistorial++;
    }
}
