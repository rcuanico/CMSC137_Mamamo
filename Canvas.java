import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class Canvas extends JPanel {

ArrayList<Point> moves = new ArrayList<Point>();

public Canvas() {

    this.init();
    this.frameInit();

}

private void init() {

    this.setPreferredSize(new Dimension(600,600));
    this.addMouseListener(new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {
            moves.add(e.getPoint());
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // if (!currentLine.points.isEmpty()) {
            
            // }
            // currentLine = null;
        }
    });


    this.addMouseMotionListener(new MouseAdapter() {

        public void mousePressed(MouseEvent e) {
            moves.add(e.getPoint());
            repaint();
        }

        public void mouseDragged(MouseEvent e) {

            moves.add(e.getPoint());
            repaint();

        }

        public void mouseReleased(MouseEvent e) {
        }

    });

}

private void frameInit() {

    JFrame window = new JFrame("GPaint");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setContentPane(this);
    window.pack();
    window.setVisible(true);

}

public void paintComponent(Graphics g) {

    Graphics2D twoD = (Graphics2D) g;
    twoD.setColor(Color.RED);
    twoD.setStroke(new BasicStroke(6));
    if(!moves.isEmpty()) {

        for(Point l: moves) {
            twoD.fillOval((int)l.getX(), (int)l.getY(), 10,10);

        }

    }



}

}



