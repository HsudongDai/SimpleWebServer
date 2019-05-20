package cn.edu.tju;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GUI extends JFrame implements ActionListener {
    //definition of visible elements
//    private JPanel canvas = new JPanel();
    private JButton start = new JButton("Start", new ImageIcon("icon/start"));
    private JButton stop = new JButton("Stop", new ImageIcon("icon/stop"));
    private JButton clear = new JButton("Clear", new ImageIcon("icon/clear"));
    private JLabel state = new JLabel("OFF now------");
    private JTextField outPort = new JTextField("8000", 4);

    private JTextArea textArea = new JTextArea();
    //  definition of the sockets
    private ServerSocket serverSocket = null;
    private Socket client = null;
    private ShowTime showTime = new ShowTime();
    private int count = 1;

    GUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Simple Web Server");
        JScrollPane scrollPane = new JScrollPane(textArea);
        JLabel por = new JLabel("Port: ");

        Font head = new Font("Verdana", 3, 28);
        Font font = new Font("Source Code Pro", Font.ITALIC, 18);

        Box box1 = Box.createHorizontalBox();
        Box box2 = Box.createHorizontalBox();
        Box hBox = Box.createHorizontalBox();
        Box box4 = Box.createHorizontalBox();
        Box box5 = Box.createHorizontalBox();

        box2.setMaximumSize(new Dimension(400, 30));

        start.setMinimumSize(new Dimension(100, 80));
        stop.setMinimumSize(new Dimension(100, 80));
        clear.setMinimumSize(new Dimension(100, 80));
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFont(font);
        state.setFont(head);

        box1.add(state);
        box2.add(por);
        box2.add(outPort);
        hBox.add(scrollPane);
        box4.add(start);
        box4.add(stop);
        box4.add(clear);
        box5.add(showTime);

        Box vBox = Box.createVerticalBox();
        //this.setBounds(200, 200, 600, 400);
        vBox.add(box1);
        vBox.add(box2);
        vBox.add(hBox);
        vBox.add(box4);
        vBox.add(box5);
        setContentPane(vBox);

        pack();
        /* old BorderLayout
        setBounds(200, 100, 600, 400);
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.WEST);
        add(showTime, BorderLayout.SOUTH);

        canvas.add(state);
        canvas.add(new JLabel("Port: "));
        canvas.add(outPort);
        canvas.add(start);
        canvas.add(stop);
        canvas.add(clear);
*/
        // cannot use when initializing
        stop.setEnabled(false);
        start.addActionListener(this);
        clear.addActionListener(this);
        stop.addActionListener(this);

        setVisible(true);
        setPreferredSize(new Dimension(640, 360));
        setMinimumSize(new Dimension(360, 200));
        setMinimumSize(new Dimension(900, 600));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        int port = Integer.parseInt(outPort.getText());
        if (ae.getSource().equals(start)) {
            state.setText("Working------");
            start.setEnabled(false);
            stop.setEnabled(true);
            try {
                serverSocket = new ServerSocket(port);
                textArea.insert("\n Server is working now---\n", 0);
                new Thread(() -> {
                    try {
                        while (true) {
                            client = serverSocket.accept();
                            textArea.insert("\n--- Request Received ---" + (count++) + '\n', 0);
                            textArea.insert(" Time: " + showTime.getTime() + '\n', 0);
                            new ServerResponse(client, count, textArea).start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (ae.getSource() == stop) {
            try{
                if (serverSocket != null) {
                    serverSocket.close();
                    textArea.insert("Server is off now---\n", 0);
                }
            }catch(IOException ioE){
                ioE.printStackTrace();
            }
            state.setText("---OFF now---");
            stop.setEnabled(false);
            start.setEnabled(true);
            // reset counter back to 1
            count = 1;
            JOptionPane.showMessageDialog(this, "Server is successfully turned off");

        }
        if (ae.getSource() == clear){
            textArea.setText("");
        }
    }
}
