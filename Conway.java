import java.awt.event.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Conway {

    public static void main(String[] args) {
        Thread Th1 = new Thread() {

            @Override
            public void run() {
                GetValGui valG = new GetValGui();
                valG.Build();
            }
        };

        Th1.start();
    }

    static public class GetValGui {

        JTextField xField;
        JTextField yField;

        public void Build() {
            JFrame f = new JFrame();
            JPanel p = new JPanel(null);

            JButton InpButton = new JButton();
            ExitButtonListener bilistener = new ExitButtonListener(f);
            InpButton.addMouseListener(bilistener);

            FopenButtonListener fblistener = new FopenButtonListener(f);
            JButton fbutton = new JButton();
            fbutton.addMouseListener(fblistener);

            JLabel inflabl = new JLabel();
            xField = new JTextField(10);
            yField = new JTextField(10);

            xField.setBounds(30, 40, 150, 30);
            yField.setBounds(30, 80, 150, 30);

            JLabel xLabel = new JLabel();
            JLabel yLabel = new JLabel();

            xLabel.setBounds(190, 40, 120, 30);
            yLabel.setBounds(190, 80, 120, 30);

            xLabel.setText("X axis size");
            yLabel.setText("Y axis size");

            xLabel.setOpaque(true);
            yLabel.setOpaque(true);

            inflabl.setBounds(30, 10, 230, 30);
            inflabl.setText("Around (50,50) works quite well");

            InpButton.setBounds(30, 130, 110, 30);
            InpButton.setText("Enter Choices");

            fbutton.setBounds(150, 130, 110, 30);
            fbutton.setText("Open saved");


            p.add(xField);
            p.add(fbutton);
            p.add(yField);
            p.add(xLabel);
            p.add(yLabel);
            p.add(InpButton);
            p.add(inflabl);

            f.getContentPane().add(p);
            f.setTitle("Enter inital world info");
            f.setSize((290), (200));
            f.setResizable(true);
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        }

        class FopenButtonListener extends MouseAdapter {

            private final JFrame frame;

            FopenButtonListener(final JFrame frame) {
                this.frame = frame;
            }

            @Override
            public void mousePressed(final MouseEvent me) {

                final JFileChooser fc = new JFileChooser();
                //int returnVal = fc.showOpenDialog(me.getComponent());

                if (fc.showOpenDialog(me.getComponent()) == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    new Conway.MainGuiCl().MainGUICl(file);
                    frame.dispose();
                }
            }
        }

        class ExitButtonListener extends MouseAdapter {

            private final JFrame frame;

            ExitButtonListener(JFrame frame) {
                this.frame = frame;
            }

            @Override
            public void mousePressed(MouseEvent me) {
                int xSize = Integer.parseInt(xField.getText());
                int ySize = Integer.parseInt(yField.getText());

                new Conway.MainGuiCl().MainGUICl(xSize, ySize);
                frame.dispose();
            }
        }
    }

    static public class helpGUI {

        public void Build() {
            JFrame f = new JFrame();
            JPanel p = new JPanel(null);
            JButton exitbutton = new JButton();
            ExitButtonListener blistener = new ExitButtonListener(f);

            JLabel Label = new JLabel();

            Label.setBounds(0, 0, 310, 230);

            Label.setText("<html>Click on the blue area to set change the <br>state of squares white squares are \"dead\" whilst black ones are \"living\". <br>"
                    + "<br>Use the slider to change the speed."
                    + "<br>"
                    + "<br>If a living cell has more than 3 or fewer than 2 living neighbours it dies next tick"
                    + "<br>whilst if a dead cell has three living members it comes to life"
                    + "<br>"
                    + "<br>Many interesting patterns have been discovered"
                    + "<br>I advise you look at the Wikipedia page"
                    + "<br>for more information.");

            Label.setOpaque(true);

            exitbutton.setBounds(55, 240, 180, 30);
            exitbutton.setText("Exit Help");
            exitbutton.addMouseListener(blistener);
            p.add(Label);
            p.add(exitbutton);

            f.getContentPane().add(p);
            f.setTitle("Help");
            f.setSize(310, 310);
            f.setResizable(true);
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        }

        class ExitButtonListener extends MouseAdapter {

            private final JFrame frame;

            ExitButtonListener(JFrame frame) {
                this.frame = frame;
            }

            @Override
            public void mousePressed(MouseEvent me) {
                frame.dispose();
            }
        }
    }

    public static class MainGuiCl {

        private world env;
        boolean started = false;
        JLabel[][] JLArray;
        int fps;

        public class ButtonHelpListener extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent me) {
                started = false;
                helpGUI help = new helpGUI();
                help.Build();
            }
        }

        public class ButtonStartListener extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent me) {
                if (started == false) {
                    started = true;

                    for (int i = 0; i < JLArray.length; ++i) {
                        for (int j = 0; j < JLArray[i].length; ++j) {
                            env.setSquare(i, j, (JLArray[i][j].getBackground() == Color.black));
                        }
                    }
                    Thread Th = new Thread() {

                        @Override
                        public void run() {
                            while (true) {
                                if (started) {
                                    try {
                                        Thread.sleep(1000 / fps);

                                    } catch (InterruptedException e) {
                                        return;
                                    }
                                    env.update();
                                    updateGUI(JLArray, env);
                                }
                            }
                        }
                    };
                    Th.start();
                }
            }
        }

        public class ButtonPauseListener extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent me) {
                started = false;
            }
        }

        private void updateGUI(JLabel[][] JLA, world en) {

            class Thread1 extends Thread {

                public void run(JLabel[][] JL, world e) {
                    for (int i = 0; i < JL.length; ++i) {
                        for (int j = 0; j < JL[i].length; ++j) {
                            if (e.getSquare(i, j)) {
                                JL[i][j].setBackground(Color.black);
                            } else {
                                JL[i][j].setBackground(Color.white);
                            }
                        }
                    }
                }
            }

            Thread1 th = new Thread1();
            th.run(JLA, en);
        }

        class LabelListener extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent me) {
                if (!started) {
                    if (((JLabel) me.getSource()).getBackground() == Color.white) {
                        ((JLabel) me.getSource()).setBackground(Color.black);
                    } else {
                        ((JLabel) me.getSource()).setBackground(Color.white);
                    }
                }
            }
        }

        class SliderListener implements ChangeListener {

            @Override
            public void stateChanged(ChangeEvent e) {
                started = false;
                JSlider source = (JSlider) e.getSource();
                //if (!source.getValueIsAdjusting()) {
                fps = (int) source.getValue();
                //}

                if (fps != 0) {
                    started = true;
                }
            }
        }

        public class ButtonSaveListener extends MouseAdapter {

            JLabel[][] J;

            ButtonSaveListener(JLabel[][] JL) {
                this.J = JL;
            }

            @Override
            public void mousePressed(MouseEvent me) {
                started = false;
                final JFileChooser fc = new JFileChooser();
                if (fc.showSaveDialog(me.getComponent()) == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                        out.println(Integer.toString(J.length));
                        out.println(Integer.toString(J[1].length));
                        int num = 0;
                        for (int i = 0; i < J.length; ++i) {
                            for (int j = 0; j < J[i].length; ++j) {
                                if (J[i][j].getBackground() == Color.black) {
                                    ++num;

                                }
                            }
                        }
                        out.println(Integer.toString(num));
                        for (int i = 0; i < J.length; ++i) {
                            for (int j = 0; j < J[i].length; ++j) {
                                if (J[i][j].getBackground() == Color.black) {
                                    out.println(Integer.toString(i));
                                    out.println(Integer.toString(j));
                                }
                            }
                        }
                        out.flush();
                    } catch (IOException ex) {
                        Logger.getLogger(Conway.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }

        public class ButtonReListener extends MouseAdapter {

            JFrame frame;

            ButtonReListener(JFrame Jf) {
                frame = Jf;
            }

            @Override
            public void mousePressed(MouseEvent me) {
                started = false;
                new Conway.GetValGui().Build();
                frame.dispose();

            }
        }

        public void bmGUI(boolean[][] arr) {
            int i, j;
            env = new world(arr);

            int FPS_MIN = 0;
            int FPS_MAX = 50;
            int FPS_INIT = 25;    //initial frames per second
            fps = FPS_INIT;

            int xNum = arr.length;
            int yNum = arr[0].length;
            JFrame f = new JFrame();

            JPanel p = new JPanel(null);
            JLArray = new JLabel[xNum][yNum];
            JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, FPS_INIT);

            ButtonReListener brelistener = new ButtonReListener(f);
            LabelListener llistener = new LabelListener();
            ButtonStartListener bstlistener = new ButtonStartListener();
            ButtonPauseListener bpalistener = new ButtonPauseListener();
            ButtonHelpListener bhlistener = new ButtonHelpListener();
            ButtonSaveListener bslistener = new ButtonSaveListener(JLArray);
            SliderListener fpslistener = new SliderListener();

            JButton rebutton = new JButton();
            rebutton.addMouseListener(brelistener);

            framesPerSecond.addChangeListener(fpslistener);


            framesPerSecond.setMajorTickSpacing(10);
            framesPerSecond.setMinorTickSpacing(1);
            framesPerSecond.setPaintTicks(true);
            framesPerSecond.setPaintLabels(true);
            framesPerSecond.setBounds(15, 0, (xNum * 10) - 30, 50);

            for (i = 0; i < JLArray.length; ++i) {
                for (j = 0; j < JLArray[i].length; ++j) {
                    JLArray[i][j] = new JLabel();
                    JLArray[i][j].setOpaque(true);
                    JLArray[i][j].setBounds(10 * i, (10 * j) + 100, 10, 10);
                    JLArray[i][j].setBackground(Color.white);
                    JLArray[i][j].setBorder(BorderFactory.createLineBorder(Color.white));
                    JLArray[i][j].addMouseListener(llistener);
                    p.add(JLArray[i][j]);
                }
            }

            updateGUI(JLArray, env);

            JButton b1 = new JButton();
            b1.addMouseListener(bstlistener);
            b1.setBounds(0, 50, xNum * 5, 50);
            b1.setText("Start Run");

            JButton savebutton = new JButton();
            savebutton.addMouseListener(bslistener);
            savebutton.setBounds(0, (yNum * 10) + 100, xNum * 4, 50);
            savebutton.setText("Save Pattern");

            rebutton.setBounds(xNum * 6, (yNum * 10) + 100, xNum * 4, 50);
            rebutton.setText("Back to selection");

            JButton b2 = new JButton();
            b2.addMouseListener(bpalistener);
            b2.setBounds(xNum * 5, 50, xNum * 5, 50);
            b2.setText("Pause run");

            JButton helpbutton = new JButton();
            helpbutton.addMouseListener(bhlistener);
            helpbutton.setBounds(xNum * 4, (yNum * 10) + 100, xNum * 2, 50);
            helpbutton.setText("Help");


            p.add(b1);
            p.add(rebutton);
            p.add(b2);
            p.add(helpbutton);
            p.add(savebutton);
            p.add(framesPerSecond);

            f.getContentPane().add(p);
            f.setTitle("Conway's Life");
            f.setSize((10 * xNum), (10 * yNum) + 175);
            f.setResizable(true);
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        }

        public void MainGUICl(int xNu, int yNu) {
            boolean[][] arra = new boolean[xNu][yNu];
            bmGUI(arra);
        }

        public void MainGUICl(File f) {

            class Thread1 extends Thread {

                public void run(File fi) {
                    BufferedReader br = null;
                    String ins1 = null, ins2 = null;

                    try {
                        br = new BufferedReader(new InputStreamReader(new FileInputStream(fi), Charset.defaultCharset()));
                        ins1 = br.readLine();
                        ins2 = br.readLine();

                        int x;
                        int y;

                        boolean[][] a = new boolean[Integer.parseInt(ins1)][Integer.parseInt(ins2)];
                        for (int i = 0; i < a.length; ++i) {
                            for (int j = 0; j < a[i].length; ++j) {
                                a[i][j] = false;
                            }
                        }
                        int pointsInFile = Integer.parseInt(br.readLine());

                        for (int i = 0; i < pointsInFile; ++i) {
                            x = Integer.parseInt(br.readLine());
                            y = Integer.parseInt(br.readLine());
                            a[x][y] = true;
                        }

                        bmGUI(a);

                    } catch (Exception ex) {
                        Logger.getLogger(Conway.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException ex) {
                                Logger.getLogger(Conway.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
            Thread1 Thr = new Thread1();
            Thr.run(f);
        }

        private class world {

            int xSize;
            int ySize;
            boolean[][] array;

            boolean getSquare(int i, int j) {
                return array[i][j];
            }

            void setSquare(int i, int j, boolean life) {
                array[i][j] = life;
            }

            public world(boolean[][] ar) {
                xSize = ar.length;
                ySize = ar[0].length;
                array = new boolean[xSize][ySize];
                for (int i = 0; i < xSize; ++i) {
                    for (int j = 0; j < ySize; ++j) {
                        array[i][j] = ar[i][j];
                    }
                }
            }

            void update() {
                int i, j, num_adj;
                boolean newArray[][] = new boolean[xSize][ySize];

                for (i = 0; i < xSize; ++i) {
                    for (j = 0; j < ySize; ++j) {
                        num_adj = 0;
                        if ((i > 0) && (i < (xSize - 1)) && (j > 0) && (j < (ySize - 1))) {

                            if (array[i - 1][j - 1]) {
                                num_adj++;
                            }
                            if (array[i - 1][j]) {
                                num_adj++;
                            }
                            if (array[i - 1][j + 1]) {
                                num_adj++;
                            }
                            if (array[i][j - 1]) {
                                num_adj++;
                            }
                            if (array[i][j + 1]) {
                                num_adj++;
                            }
                            if (array[i + 1][j - 1]) {
                                num_adj++;
                            }
                            if (array[i + 1][j]) {
                                num_adj++;
                            }
                            if (array[i + 1][j + 1]) {
                                num_adj++;
                            }

                        } else if ((i == 0) && (j > 0) && (j < (ySize - 1))) {
                            if (array[(xSize - 1)][j - 1]) {
                                num_adj++;
                            }
                            if (array[(xSize - 1)][j]) {
                                num_adj++;
                            }
                            if (array[(xSize - 1)][j + 1]) {
                                num_adj++;
                            }
                            if (array[i][j - 1]) {
                                num_adj++;
                            }
                            if (array[i][j + 1]) {
                                num_adj++;
                            }
                            if (array[i + 1][j - 1]) {
                                num_adj++;
                            }
                            if (array[i + 1][j]) {
                                num_adj++;
                            }
                            if (array[i + 1][j + 1]) {
                                num_adj++;
                            }

                        } else if ((i == (xSize - 1)) && (j > 0) && (j < (ySize - 1))) {
                            if (array[i - 1][j - 1]) {
                                num_adj++;
                            }
                            if (array[i - 1][j]) {
                                num_adj++;
                            }
                            if (array[i - 1][j + 1]) {
                                num_adj++;
                            }
                            if (array[i][j - 1]) {
                                num_adj++;
                            }
                            if (array[i][j + 1]) {
                                num_adj++;
                            }
                            if (array[0][j - 1]) {
                                num_adj++;
                            }
                            if (array[0][j]) {
                                num_adj++;
                            }
                            if (array[0][j + 1]) {
                                num_adj++;
                            }

                        } else if ((j == 0) && (i > 0) && (i < (xSize - 1))) {
                            if (array[i - 1][(ySize - 1)]) {
                                num_adj++;
                            }
                            if (array[i - 1][j]) {
                                num_adj++;
                            }
                            if (array[i - 1][j + 1]) {
                                num_adj++;
                            }
                            if (array[i][(ySize - 1)]) {
                                num_adj++;
                            }
                            if (array[i][j + 1]) {
                                num_adj++;
                            }
                            if (array[i + 1][(ySize - 1)]) {
                                num_adj++;
                            }
                            if (array[i + 1][j]) {
                                num_adj++;
                            }
                            if (array[i + 1][j + 1]) {
                                num_adj++;
                            }
                        } else if ((j == (ySize - 1)) && (i > 0) && (i < (xSize - 1))) {
                            if (array[i - 1][j - 1]) {
                                num_adj++;
                            }
                            if (array[i - 1][j]) {
                                num_adj++;
                            }
                            if (array[i - 1][0]) {
                                num_adj++;
                            }
                            if (array[i][j - 1]) {
                                num_adj++;
                            }
                            if (array[i][0]) {
                                num_adj++;
                            }
                            if (array[i + 1][j - 1]) {
                                num_adj++;
                            }
                            if (array[i + 1][j]) {
                                num_adj++;
                            }
                            if (array[i + 1][0]) {
                                num_adj++;
                            }
                        } else if ((i == 0) && (j == 0)) {
                            if (array[(xSize - 1)][(ySize - 1)]) {
                                num_adj++;
                            }
                            if (array[(xSize - 1)][j]) {
                                num_adj++;
                            }
                            if (array[(xSize - 1)][j + 1]) {
                                num_adj++;
                            }
                            if (array[i][(ySize - 1)]) {
                                num_adj++;
                            }
                            if (array[i][j + 1]) {
                                num_adj++;
                            }
                            if (array[i + 1][(ySize - 1)]) {
                                num_adj++;
                            }
                            if (array[i + 1][j]) {
                                num_adj++;
                            }
                            if (array[i + 1][j + 1]) {
                                num_adj++;
                            }
                        } else if ((i == (xSize - 1)) && (j == 0)) {
                            if (array[i - 1][(ySize - 1)]) {
                                num_adj++;
                            }
                            if (array[i - 1][j]) {
                                num_adj++;
                            }
                            if (array[i - 1][j + 1]) {
                                num_adj++;
                            }
                            if (array[i][(ySize - 1)]) {
                                num_adj++;
                            }
                            if (array[i][j + 1]) {
                                num_adj++;
                            }
                            if (array[0][(ySize - 1)]) {
                                num_adj++;
                            }
                            if (array[0][j]) {
                                num_adj++;
                            }
                            if (array[0][j + 1]) {
                                num_adj++;
                            }

                        } else if ((i == 0) && (j == (ySize - 1))) {
                            if (array[(xSize - 1)][j - 1]) {
                                num_adj++;
                            }
                            if (array[(xSize - 1)][j]) {
                                num_adj++;
                            }
                            if (array[(xSize - 1)][0]) {
                                num_adj++;
                            }
                            if (array[i][j - 1]) {
                                num_adj++;
                            }
                            if (array[i][0]) {
                                num_adj++;
                            }
                            if (array[i + 1][j - 1]) {
                                num_adj++;
                            }
                            if (array[i + 1][j]) {
                                num_adj++;
                            }
                            if (array[i + 1][0]) {
                                num_adj++;
                            }

                        } else if ((i == (xSize - 1)) && (j == (ySize - 1))) {
                            if (array[i - 1][j - 1]) {
                                num_adj++;
                            }
                            if (array[i - 1][j]) {
                                num_adj++;
                            }
                            if (array[i - 1][0]) {
                                num_adj++;
                            }
                            if (array[i][j - 1]) {
                                num_adj++;
                            }
                            if (array[i][0]) {
                                num_adj++;
                            }
                            if (array[0][j - 1]) {
                                num_adj++;
                            }
                            if (array[0][j]) {
                                num_adj++;
                            }
                            if (array[0][0]) {
                                num_adj++;
                            }
                        }

                        if ((array[i][j]) == true) {
                            if ((num_adj < 2) || (num_adj > 3)) {
                                newArray[i][j] = false;
                            } else {
                                newArray[i][j] = true;
                            }
                        } else {
                            if (num_adj == 3) {
                                newArray[i][j] = true;
                            }
                        }

                    }
                }

                for (i = 0; i < xSize; ++i) {
                    for (j = 0; j < ySize; ++j) {
                        array[i][j] = newArray[i][j];

                    }
                }
            }
        }
    }
}
