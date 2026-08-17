import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

//Aqui trabajan weyes

class Operaciones {

    //suma

    public static double sumar(double a, double b) {
        //TODO: aqui va el codigo
        return a + b;
    }

    public static double restar(double a, double b) {
        // aqui va restasr
        return a-b ;
    }

    public static double multiplicar(double a, double b) {
        return a * b;
    }

    public static double dividir(double a, double b) {
        // aqui va el el codee
        return a / b;
    }
}

//con esto sorprendemos al profe 

public class calculadora extends JFrame implements ActionListener {

    private JTextField pantalla;
    private double primerNumero = 0;
    private String operador = "";
    private boolean nuevaEntrada = true;

    public calculadora () {
        setTitle("calculadora");
        setSize(320, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        pantalla = new JTextField("0");
        pantalla.setFont(new Font("Arial", Font.BOLD, 28));
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        pantalla.setEditable(false);
        add(pantalla, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(5, 4, 5, 5));

        String[] botones = {
            "C", " ", " ", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", ""
        };

        for (String texto : botones) {
            if (texto.equals(" ")) {
                panelBotones.add(new JLabel(""));
            } else if (!texto.isEmpty()) {
                JButton boton = new JButton(texto);
                boton.setFont(new Font("Arial", Font.PLAIN, 18));
                boton.addActionListener(this);
                panelBotones.add(boton);
            }
        }
            add(panelBotones, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        // Si es un número o punto decimal
        if (comando.matches("[0-9]|\\.")) {
            if (nuevaEntrada) {
                pantalla.setText(comando);
                nuevaEntrada = false;
            } else {
                pantalla.setText(pantalla.getText() + comando);
            }
        }

        else if (comando.equals("C")) {
            pantalla.setText("0");
            primerNumero = 0;
            operador = "";
            nuevaEntrada = true;
        }

        else if (comando.equals("=")) {
            double segundoNumero = Double.parseDouble(pantalla.getText());
            double resultado = 0;

            switch (operador) {
                case "+":
                    resultado = Operaciones.sumar(primerNumero, segundoNumero);
                    break;
                case "-":
                    resultado = Operaciones.restar(primerNumero, segundoNumero);
                    break;
                case "*":
                    resultado = Operaciones.multiplicar(primerNumero, segundoNumero);
                    break;
                case "/":
                    resultado = Operaciones.dividir(primerNumero, segundoNumero);
                    break;
             }

             pantalla.setText(String.valueOf(resultado));
            nuevaEntrada = true;
        } 
//#endregion la cosa de los puntos decimales es por lo del binario, como no se puede calcular exacto los numeros los pone como aproximaciones, la solucion es igular la variable de resultado, y luego multiplciarla por 100000000 y lo dividirla por ese mismo numero, despues usas la funcion de math.round. si haces eso antes de la linea de codigo 118, se recorren 8 decimales y no te da un numero todo horrible.      

                    

        else {
            primerNumero = Double.parseDouble(pantalla.getText());
            operador = comando;
            nuevaEntrada = true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new calculadora().setVisible(true);
        });
    }
}