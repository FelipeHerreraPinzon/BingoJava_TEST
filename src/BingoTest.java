// Importación de librerías necesarias
import java.util.Arrays;
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

    // matriz 2D para almacenar el historial de letras y números llamados
    private static String[][] historial;
    // Índice para seguir un registro del historial
    private static int indiceHistorial;

    private static int cantidad;

    // Método principal
    public static void main(String[] args) {

        // Declaración de variables
        int cantidadCartones = solicitarCantidadCartones();
        cantidad = cantidadCartones;
        String[] nombresCartones = new String[cantidadCartones];
        int[][] cartones = new int[cantidadCartones][25]; // representa TODOS los cartones de BINGO (parte numerica)
        boolean[] cartonLleno = new boolean[cantidadCartones]; // representa el estado de los cartones TRUE para lleno y FALSE para no lleno

        // Inicialización del historial
        historial = new String[5 * 15][2];
        indiceHistorial = 0;

        // Generación e impresión de los cartones numerados
        for (int i = 0; i < cantidadCartones; i++) {
            nombresCartones[i] = "Cartón " + (i + 1);  // este arreglo almacena como nombres de cartones los numeros del 1 a cantidadCartones
            generarCarton(cartones[i]);
            cartonLleno[i] = false;
        }
        //imprimirCartones(cartones, nombresCartones);
        imprimirNumerosCartones(cartones, nombresCartones);

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
        //imprimirCartones(cartones, nombresCartones);
        imprimirNumerosCartones(cartones, nombresCartones);

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

            // Imprimir el historial después de cada llamado
            imprimirHistorial();

            System.out.println("\nNúmero llamado: " + letra + " " + numero + "\n");

            for (int i = 0; i < cantidadCartones; i++) {
                if (!cartonLleno[i] && marcarCarton(cartones[i], letra, numero)) {
                    cartonLleno[i] = verificarBingo(cartones[i]);
                    //imprimirCartonConX(cartones[i], nombresCartones[i]);
                    if (cartonLleno[i]) {
                        JOptionPane.showMessageDialog(null, nombresCartones[i] + " hizo BINGO!!!");
                    } else {
                        JOptionPane.showMessageDialog(null, nombresCartones[i] + " coincidió con " + letra + " " + numero);
                    }
                }
            }
            imprimirNumerosCartones(cartones, nombresCartones);

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
        Random random = new Random(); // Crear una instancia de la clase Random para generar números aleatorios

        for (int i = 0; i < 5; i++) { // Iterar sobre las filas del cartón
            for (int j = 0; j < 5; j++) { // Iterar sobre las columnas del cartón
                if (j == 2 && i == 2) {
                    // Si estamos en la posición central (fila 2, columna 2), asignar 0 (espacio libre)
                    carton[i * 5 + j] = 0;
                } else {
                    int nuevoNumero;
                    boolean numeroRepetido;

                    // Generar un nuevo número para la columna actual sin repetirlo en la misma columna
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

                    // Asignar el nuevo número al cartón
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
    /**private static void imprimirCartones(int[][] cartones, String[] nombresCartones) {

     imprimirNumerosCartones(cartones, nombresCartones);

     }*/

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

    private static void imprimirNumerosCartones(int[][] cartones, String [] nombresCartones) {

        String[] columnas = {"\u001B[31mB", "\u001B[31mI", "\u001B[31mN", "\u001B[31mG", "\u001B[31mO"}; // Texto rojo para las letras BINGO
        int currentIndex = 0;

        System.out.println();

        int p1 = cantidad / 4;
        int p2 = cantidad - p1 * 4;

        for (int x = 0; x < p1; x++) {
            currentIndex = 0;

            for (int i = 0; i < 4; i++) {

                String n = String.format("%-27s", (nombresCartones[i+x*4] + ":"));
                System.out.print("\u001B[34m" + n + "\u001B[0m" + "\t");
            }
            System.out.println();


            for (int c = 0; c < 4; c++) {
                for (int i = 0; i < columnas.length; i++) {
                    System.out.print(columnas[i] + "\t" + "\u001B[0m");
                }
                System.out.print("\t\t");
            }

            System.out.println();

            for (int i = 0; i < 5; i++) {
                //System.out.println(i);
                for (int c = 0; c < 4; c++) {
                    for (int j = 0; j < columnas.length; j++) {
                        int numero = cartones[c][currentIndex+j];
                        if (numero == 0) {
                            System.out.print(ANSI_GREEN_BACKGROUND + "X" + ANSI_RESET + "\t"); // Fondo verde
                        } else {
                            System.out.print(numero + "\t");
                        }
                    }

                    System.out.print("\t\t");
                }
                currentIndex+=5;
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }


        currentIndex = 0;

        if (p2 != 0) {
            for (int i = 0; i < p2; i++) {
                String n = String.format("%-27s", (nombresCartones[i+p1*4] + ":"));
                System.out.print("\u001B[34m" + n + "\u001B[0m" + "\t\t\t\t");
            }
            System.out.println();

            for (int c = 0; c < p2; c++) {
                for (int i = 0; i < columnas.length; i++) {
                    System.out.print(columnas[i] + "\t" + "\u001B[0m");
                }
                System.out.print("\t\t");
            }

            System.out.println();

            for (int i = 0; i < 5; i++) {
                for (int c = 0; c < p2; c++) {
                    for (int j = 0; j < columnas.length; j++) {
                        int numero = cartones[c+p1*4][currentIndex+j];
                        if (numero == 0) {
                            System.out.print(ANSI_GREEN_BACKGROUND + "X" + ANSI_RESET + "\t"); // Fondo verde
                        } else {
                            System.out.print(numero + "\t");
                        }
                    }

                    System.out.print("\t\t");
                }
                currentIndex+=5;
                System.out.println();
            }
            System.out.println();
        }
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
        // Verificar filas
        for (int i = 0; i < 5; i++) {
            boolean filaCompleta = true;
            for (int j = 0; j < 5; j++) {
                if (carton[i * 5 + j] != 0) {
                    filaCompleta = false;
                    break;
                }
            }
            if (filaCompleta) {
                return true;
            }
        }

        // Implementa la lógica para verificar un BINGO en columnas y diagonales aquí
        // ...

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
    // Método para imprimir el historial de números y letras

    // Método para imprimir el historial de números y letras
    private static void imprimirHistorial() {
        System.out.println("Historial de números y letras llamados:");
        System.out.println("-------------------------------------");

        // Obtener la lista única de letras en el historial
        String[] letrasUnicas = obtenerLetrasUnicas();

        // Imprimir el historial agrupado por letra
        for (String letra : letrasUnicas) {
            System.out.print(letra + "   ");
            imprimirNumerosPorLetra(letra);
            System.out.println();
        }

        System.out.println("-------------------------------------\n");
    }

    // Método para obtener la lista única de letras en el historial
    private static String[] obtenerLetrasUnicas() {
        String[] letrasUnicas = new String[indiceHistorial];
        int count = 0;

        for (int i = 0; i < indiceHistorial; i++) {
            String letra = historial[i][0];
            if (!contiene(letrasUnicas, letra, count)) {
                letrasUnicas[count++] = letra;
            }
        }

        // Ajustar el tamaño del arreglo resultante
        String[] resultado = new String[count];
        System.arraycopy(letrasUnicas, 0, resultado, 0, count);

        return resultado;
    }

    // Método para verificar si un arreglo contiene un elemento específico
    private static boolean contiene(String[] arreglo, String elemento, int count) {
        for (int i = 0; i < count; i++) {
            if (arreglo[i].equals(elemento)) {
                return true;
            }
        }
        return false;
    }

    // Método para imprimir los números asociados a una letra en el historial
    private static void imprimirNumerosPorLetra(String letra) {
        for (int i = 0; i < indiceHistorial; i++) {
            if (historial[i][0].equals(letra)) {
                System.out.print(historial[i][1] + " ");
            }
        }
    }
}