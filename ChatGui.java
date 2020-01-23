import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;

class ChatGui extends JFrame {
    Socket mySocket;
    DataInputStream dis;
    PrintStream ps;

    JTextArea ta;
    String name;

    private static final long serialVersionUID = 1L;

    public ChatGui() {

        this.setLayout(new FlowLayout());
        setTitle("ChatGui Rooming");

        ta = new JTextArea(20, 50);
        JScrollPane scroll = new JScrollPane(ta);
        ta.setEditable(false);

        JTextField tf = new JTextField(40);
        JButton okButton = new JButton("send");

        add(scroll);
        add(tf);
        add(okButton);
        name = JOptionPane.showInputDialog(this, "Enter your name");

        try {
            mySocket = new Socket(InetAddress.getLocalHost(), 5000);
            dis = new DataInputStream(this.mySocket.getInputStream());
            ps = new PrintStream(mySocket.getOutputStream());

        } catch (IOException e) {
            System.out.println("cant connect");
        }

        ReadThread th = new ReadThread();
        th.start();

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sendMsg(tf);
            }
        });

        tf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                sendMsg(tf);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // JOptionPane.showMessageDialog(this, "Welcome to chat Rooming");

    }

    public void sendMsg(JTextField tf) {
        if (!(tf.getText().equals(""))) {
            try {
                String s = name + " : " + tf.getText();
                ps.println(s);
                tf.setText("");
            } catch (Exception e) {
                System.out.println("cant send");
            }
        }
    }

    public static void main(String[] args) {
        ChatGui sc = new ChatGui();
        sc.setSize(600, 400);
        sc.setResizable(false);
        sc.setVisible(true);
    }

    class ReadThread extends Thread {
        // ta.append(name + " :" + str + "\n");
        public void run() {
            String str = null;
            while (true) {
                try {
                    str = dis.readLine();
                    System.out.println(str);
                } catch (IOException e1) {
                    try {
                        ps.close();
                        dis.close();
                        mySocket.close();
                        // System.exit(0);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    } finally {
                        JOptionPane.showMessageDialog(ChatGui.this, "Server is down");
                        System.exit(0);
                    }
                    break;
                }
                ChatGui.this.ta.append(str + "\n");
            }
        }

    }
}
