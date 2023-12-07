import javax.swing.*;
import java.awt.*;

public class CuadroGameOver extends JDialog {
    private boolean reiniciar;

    public CuadroGameOver(JFrame padre, int puntaje) {
        // Se crea el diálogo de Game Over
        super(padre, "Game Over", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(200, 200);
        setLocationRelativeTo(padre);

        // Panel creado
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel etiquetaMensaje = new JLabel("Game Over\n\nPuntuacion: " + puntaje);
        etiquetaMensaje.setHorizontalAlignment(JLabel.CENTER);
        panel.add(etiquetaMensaje, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Se agregan imágenes como botones
        ImageIcon iconoReiniciar = new ImageIcon("boton4.png");
        JButton botonReiniciar = new JButton(iconoReiniciar);
        botonReiniciar.setPreferredSize(new Dimension(60, 60));
        botonReiniciar.addActionListener(e -> {
            reiniciar = true;
            dispose();
        });

        ImageIcon iconoSalir = new ImageIcon("boton5.png");
        JButton botonSalir = new JButton(iconoSalir);
        botonSalir.setPreferredSize(new Dimension(60, 60));
        botonSalir.addActionListener(e -> {
            reiniciar = false;
            dispose();
        });

        // Se agregan los botones al panel
        panelBotones.add(botonReiniciar);
        panelBotones.add(botonSalir);

        panel.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    public boolean reiniciar() {
        return reiniciar;
    }
}
