import javax.swing.*;

public class DoodleJump {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Alien Jump");
            ventana.setResizable(false);
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.add(new GestorJuego());
            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }

    
}
