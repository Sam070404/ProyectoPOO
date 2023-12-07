import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class PosicionPlataforma {
    int x, y;
}

public class GestorJuego extends JPanel implements Runnable, KeyListener, ActionListener {
   
    final int ANCHO = 400;
    final int ALTO = 600;
    private int x = 100, y = 100, h = 150;
    private float dy = 0;
    private boolean derecha, izquierda;
    private PosicionPlataforma[] posicionesPlataformas;


    private boolean juegoIniciado = false;
    private boolean enEjecucion;
    private int puntaje = 0;
    private boolean juegoTerminado = false;

    private Thread hilo;
    private BufferedImage vista, fondo, plataforma, doodle;
    private JButton botonInicio;
    private JLabel etiquetaPuntuacion;
    private GestorSonido gestorSonido;


    ExecutorService servicioEjecutor = Executors.newSingleThreadExecutor();

    public GestorJuego(){
        setPreferredSize(new Dimension(ANCHO, ALTO));
        addKeyListener(this);
        setLayout(new GridBagLayout());

        // Fondo de inicio
        ImageIcon iconoFondo = new ImageIcon(getClass().getResource("espacio4.jpeg"));
        Image imagenFondo = iconoFondo.getImage().getScaledInstance(ANCHO, ALTO, Image.SCALE_SMOOTH);
        JLabel etiquetaFondo = new JLabel(new ImageIcon(imagenFondo));

        // Botón de inicio
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("boton1.png"));
        Image imagenRedimensionada = iconoOriginal.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon iconoRedimensionado = new ImageIcon(imagenRedimensionada);
        botonInicio = new JButton(iconoRedimensionado);

        // Puntuación
        etiquetaPuntuacion = new JLabel("Puntuación: 100");
        etiquetaPuntuacion.setForeground(Color.WHITE);

        // Posición de la puntuación
        GridBagConstraints gbcPuntuacion = new GridBagConstraints();
        gbcPuntuacion.gridx = 1;
        gbcPuntuacion.gridy = 0;
        gbcPuntuacion.anchor = GridBagConstraints.NORTHEAST;

        // Necesario para quitar el borde del botón
        botonInicio.setBorder(new EmptyBorder(0, 0, 0, 0));
        botonInicio.setContentAreaFilled(false);
        botonInicio.addActionListener(this);

        // Necesario para centrar el botón
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        add(botonInicio, gbc);
        add(etiquetaFondo, gbc);
        add(etiquetaPuntuacion, gbc);
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonInicio) {
            iniciarJuego();
        }
    }

    private void iniciarJuego() {
        remove(botonInicio);
        revalidate();
        juegoIniciado = true;  // El juego comienza
        puntaje = 0; // Se inicializa el puntaje
        juegoTerminado = false;

        // Detener el hilo del juego actual antes de iniciar uno nuevo
        // Necesario para reiniciar el juego
        if (hilo != null && hilo.isAlive()) {
            enEjecucion = false;
            try {
                hilo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Restablecer las variables para permitir un reinicio exitoso
        enEjecucion = true;
        juegoTerminado = false;
        x = 100;
        y = 100;
        dy = 0;

        // Creación del audio
        gestorSonido = new GestorSonido("MusicaFondo.wav");
        gestorSonido.reproducirEnBucle();

        // Iniciar un nuevo hilo
        hilo = new Thread(this);
        hilo.start();

        // Asegurar que el panel tenga el foco del teclado
        requestFocusInWindow();
        requestFocus();
    }

    public void start() {
        try {
            vista = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);

            // Posibles cambios de apariencia básica (Si son necesarios)
            fondo = ImageIO.read(getClass().getResource("espacio1.jpeg"));
            plataforma = ImageIO.read(getClass().getResource("plataforma.png"));
            doodle = ImageIO.read(getClass().getResource("Marcian2.png"));

            posicionesPlataformas = new PosicionPlataforma[30];

            for (int i = 0; i < 10; i++) {
                posicionesPlataformas[i] = new PosicionPlataforma();
                posicionesPlataformas[i].x = new Random().nextInt(400);
                posicionesPlataformas[i].y = new Random().nextInt(600);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    @Override
    public void run() {
        try {
            start();
            while (enEjecucion && juegoIniciado && !juegoTerminado) {
                actualizar();
                actualizarPuntuacion();
                dibujar();
                Thread.sleep(1000 / 60);
            }

            if (juegoIniciado) {
                gestorSonido.detener();
                // Se muestra el cuadro de game over
                SwingUtilities.invokeLater(() -> {
                    CuadroGameOver dialogo = new CuadroGameOver((JFrame) SwingUtilities.getWindowAncestor(this), puntaje);
                    dialogo.setVisible(true);

                    if (dialogo.reiniciar()) {
                        // Se reinicia llamando a iniciarJuego()
                        iniciarJuego();
                    } else {
                        System.exit(0);  // Salida del programa
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Gestión de movimientos, salto y puntaje
    public void actualizar() {
        if (!juegoTerminado) {
            if (derecha) {
                x += 3;
            } else if (izquierda) {
                x -= 3;
            }

            dy += 0.2;
            y += dy;

            if (y > ALTO) {
                juegoTerminado = true;
            }

            if (y < h) {
                for (int i = 0; i < 10; i++) {
                    y = h;
                    posicionesPlataformas[i].y = posicionesPlataformas[i].y - (int) dy;
                    if (posicionesPlataformas[i].y > ALTO) {
                        posicionesPlataformas[i].y = 0;
                        posicionesPlataformas[i].x = new Random().nextInt(ANCHO);
                    }
                }
            }

            for (int i = 0; i < 10; i++) {
                if ((x + 50 > posicionesPlataformas[i].x) &&
                        (x + 20 < posicionesPlataformas[i].x + 68) &&
                        (y + 50 > posicionesPlataformas[i].y) &&
                        (y + 50 < posicionesPlataformas[i].y + 14) &&
                        (dy > 0)) {
                    dy = -10;
                    puntaje += 1;  // Aumentar el puntaje al saltar sobre una plataforma
                }
            }
        }
    }

    // Gestionar la actualización del puntaje
    private void actualizarPuntuacion() {
        etiquetaPuntuacion.setText("Puntuación: " + puntaje);
    }

    public void dibujar() {
        Graphics2D g2 = (Graphics2D) vista.getGraphics();
        g2.drawImage(fondo, 0, 0, ANCHO, ALTO, this);
        // Ajustes en el tamaño del personaje
        g2.drawImage(doodle, x, y, 70, 70, this);
        for (int i = 0; i < 10; i++) {
            g2.drawImage(
                    // Ajustes en la aparición de la plataforma
                    plataforma,
                    posicionesPlataformas[i].x,
                    posicionesPlataformas[i].y,
                    100, 30,
                    null
            );
        }
        // Dibujar la puntuación en la esquina superior derecha
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Puntuacion: " + puntaje, ANCHO - 120, 20);
        Graphics g = getGraphics();
        g.drawImage(vista, 0, 0, ANCHO, ALTO, null);
        g.dispose();
    }

    // Gestión de entrada por teclado 
    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Responde por la tecla seleccionada
    @Override
    public void keyPressed(KeyEvent e) {
        if (juegoIniciado) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                derecha = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                izquierda = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (juegoIniciado) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                derecha = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                izquierda = false;
            }
        }
    }
}