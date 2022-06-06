package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener {

	private Dimension d;
    private final Font smallFont = new Font("Helvetica", Font.BOLD,14);
    private boolean inGame = false;
    private boolean dying = false;

    //tamaño y numero de bloques, numero de fantasmas, cerezas y velocidad de pacman
    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int MAX_GHOSTS = 8;
    private final int MAX_CHERRY = 3;
    private final int PACMAN_SPEED = 6;
    private int N_GHOSTS = 4;
    private int N_CHERRY = 1;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;
    private int[] cherry_x, cherry_y, cherry_dx, cherry_dy, cherrySpeed;
    //declaracion de imagenes
    private Image heart, ghost, cherry;
    private Image up, down, left, right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy;
 
    private final short levelData[] = {
    	19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22, // tablero de pacman
        17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20, //0=bloques azules o paredes
        17, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,  //1=borde izquierdo
        21,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20, //2= borde superior
        17, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20, //4=borde derecho
        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21, //8=borde inferior
        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21, //16= dots
        17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
        17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
        21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };
    
    
    
   //velocidades que pueden tener los iconos, ya que mas adelante se randomizaran
    private final int validSpeeds[] = {1, 2, 3,4,6};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    public Model() { //aqui se cargaran las imagenes, las variables, las funciones del teclado
                     //y se iniciará el juego
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }
    //metodo para cargar imagenes desde el directorio raiz
    private void loadImages() {
    	down = new ImageIcon("C:\\Users\\rober\\OneDrive\\Documents\\Eclipse\\Pacman\\src\\images/down.gif").getImage();
    	up = new ImageIcon("C:\\Users\\rober\\OneDrive\\Documents\\Eclipse\\Pacman\\src\\images/up.gif").getImage();
    	left = new ImageIcon("C:\\Users\\rober\\OneDrive\\Documents\\Eclipse\\Pacman\\src\\images/left.gif").getImage();
    	right = new ImageIcon("C:\\Users\\rober\\OneDrive\\Documents\\Eclipse\\Pacman\\src\\images/right.gif").getImage();
        ghost = new ImageIcon("C:\\Users\\rober\\OneDrive\\Documents\\Eclipse\\Pacman\\src\\images/ghost2.png").getImage();
        heart = new ImageIcon("C:\\Users\\rober\\OneDrive\\Documents\\Eclipse\\Pacman\\src\\images/heart.png").getImage();
        cherry = new ImageIcon("C:\\Users\\rober\\OneDrive\\Documents\\Eclipse\\Pacman\\src\\images/cherry.png").getImage();

    }
    //aqui se inicializan las variables de la pantalla, las dimensiones y el maximo de objetos
       private void initVariables() {
        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(800, 800);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        cherry_x = new int[MAX_CHERRY];
        cherry_dx = new int[MAX_CHERRY];
        cherry_y = new int[MAX_CHERRY];
        cherry_dy = new int[MAX_CHERRY];
        cherrySpeed = new int[MAX_CHERRY];
        
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(40, this);
        timer.start();
    }

    private void playGame(Graphics2D g2d) {

        if (dying) {
            death();
        } else {
            movePacman(); //al iniciar el juego se inician los metodos creados anteriormente
            drawPacman(g2d);
            moveGhosts(g2d);
            moveCherries(g2d);
            checkMaze();
            //checkMaze2();
        }
    }
    //aqui se muestra el texto inicial para empezar a jugar
    private void showIntroScreen(Graphics2D g2d) {
    	String start1 = "PRESIONA ESPACIO PARA JUGAR!";
    	g2d.setColor(Color.red);
        g2d.drawString(start1, (SCREEN_SIZE)/6, 395);
    }
    //mostrar puntaje
    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(Color.white);
        String s = "Puntaje: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);
        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
        
    }
  //aqui se muestran los textos dentro del juego
    private void texts(Graphics2D g2d) {
    	String text = "PACMAN EDUCATIVO";
        String text2 = "Universidad Autonoma de Baja California";
        g2d.setColor(Color.yellow);
        g2d.drawString(text,(int) ((SCREEN_SIZE)/3.2), 415);
        g2d.drawString(text2,(SCREEN_SIZE)/8, 430);
    }
    //instrucciones del juego
    private void Instrucciones(Graphics2D g2d) {
        String show1 = "INSTRUCCIONES:";
        String shows = "-Tienes 3 vidas";
    	String show2 = "-Trata de comer todos los dots";
    	String show3 = "-Al comer la cereza aumentas 100 de puntaje";
    	String show4 = "-A tu izquierda se mostrara informacion educativa";
    	String showss ="-Utiliza las flechas de tu teclado para moverte";
    	String show5 = "¡SUERTE!";
        g2d.setColor(Color.white);
        g2d.drawString(show1, (SCREEN_SIZE)/3,460);
        g2d.drawString(shows, (SCREEN_SIZE)/19,480);
        g2d.drawString(show2, (SCREEN_SIZE)/19,500);
        g2d.drawString(show3, (SCREEN_SIZE)/19,520);
        g2d.drawString(show4, (SCREEN_SIZE)/19,540);
        g2d.drawString(showss, (SCREEN_SIZE)/19,560);
        g2d.drawString(show5, (int) ((SCREEN_SIZE)/2.5),580);
    }

    private void checkMaze() {
//iniciamos con una variable en 0, mientras esta sea menos que el numero de bloques
//y el screendata sea diferente de 0, la variable incrementa
        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {
//al terminar, el score aumenta, los fantasmas, cereza y velocidad de cada uno se reinicia
//asi como el nivel
            score+=10;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }
            if (N_CHERRY < MAX_CHERRY) {
                N_CHERRY++;
            }
            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }
//si las vidas totales son igual a 0, el juego se acaba y se reinicia
//reiniciando tambien cada objeto
    private void death() {

    	lives--;

        if (lives == 0) {
            inGame = false;
        }

        continueLevel();
    }
   

//metodo para el movimiento de los fantasmas
    private void moveGhosts(Graphics2D g2d) {

        int pos;
        int count;

        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }
 //metodo para el movimiento de la cereza
    private void moveCherries(Graphics2D g2d) {

        int pos;
        int count;

        for (int i = 0; i < N_CHERRY; i++) {
        	if (cherry_x[i] % BLOCK_SIZE == 0 && cherry_y[i] % BLOCK_SIZE == 0) {
                pos = cherry_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (cherry_y[i] / BLOCK_SIZE);
                
                count = 0;

                if ((screenData[pos] & 1) == 0 && cherry_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && cherry_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && cherry_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && cherry_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        cherry_dx[i] = 0;
                        cherry_dy[i] = 0;
                    } else {
                        cherry_dx[i] = -cherry_dx[i];
                        cherry_dy[i] = -cherry_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                        //score=score+100;
                    }

                    cherry_dx[i] = dx[count];
                    cherry_dy[i] = dy[count];
                }

            }

            cherry_x[i] = cherry_x[i] + (cherry_dx[i] * cherrySpeed[i]);
            cherry_y[i] = cherry_y[i] + (cherry_dy[i] * cherrySpeed[i]);
            drawCherry(g2d, cherry_x[i] + 1, cherry_y[i] + 1);

            if (pacman_x > (cherry_x[i] - 12) && pacman_x < (cherry_x[i] + 12)
                    && pacman_y > (cherry_y[i] - 12) && pacman_y < (cherry_y[i] + 12)
                    && inGame) {

            	score=score+50;

            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {
    	g2d.drawImage(ghost, x, y, this);
        }
    
    
    private void drawCherry(Graphics2D g2d, int x, int y) {
    	g2d.drawImage(cherry, x, y, this);
    	
        }
//metodo para mover el pacman
    private void movePacman() {

        int pos;
        short ch;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos]; //posicion inicial

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++; //score aumenta al comer un dot
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        } 
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }
//metodo para dibujar el pacman, donde se mostrara una imagen diferente dependiendo el movimiento
    private void drawPacman(Graphics2D g2d) {

        if (req_dx == -1) {
        	g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
        	g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
        	g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
        	g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }
//metodo para dibujar el laberinto
    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(0,72,251));
                g2d.setStroke(new BasicStroke(5));
                
                if ((levelData[i] == 0)) { 
                	g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                 }

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
               }

                i++;
            }
        }
    }

    private void initGame() {
//estos datos se mostraran dentro del juego, al iniciarse
    	lives = 3;
        score = 0;
        initLevel();
        //initLevel2();
        N_GHOSTS = 4;
        currentSpeed = 3;
        N_CHERRY=1;
    }

//inicializacion del nivel
	private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }
        continueLevel();
    }

    private void continueLevel() {

    	int dx = 1;
        int random;

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE; //posicion inicial
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }
        
        for (int i = 0; i < N_CHERRY; i++) {

            cherry_y[i] = 6 * BLOCK_SIZE; //posicion inicial
            cherry_x[i] = 6 * BLOCK_SIZE;
            cherry_dy[i] = 0;
            cherry_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            cherrySpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * BLOCK_SIZE;  //posicion inicial de pacman
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;	//resetear direccion de movimientos
        pacmand_y = 0;
        req_dx = 0;		//resetear direccion de controles
        req_dy = 0;
        dying = false;
    }
 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        //drawMaze2(g2d);
        drawScore(g2d);

        if (inGame) {
        	//al iniciar el juego se mostraran los textos e instrucciones
            playGame(g2d);
            texts(g2d);
            Instrucciones(g2d);
            //Preguntas(g2d);
            
        } else {
        	//al morir se reiniciaran los textos y se mostraran de nuevo
            showIntroScreen(g2d);
            texts(g2d);
            Instrucciones(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

	//declaracion de controles para mover el pacman con las flechas del teclado
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } 
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
}

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
		
	}