package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ConcurrentGUI {
    
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JFrame frame = new JFrame();
    final JLabel display = new JLabel("0");
    
    public ConcurrentGUI() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.frame.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        
        final JPanel panel = new JPanel();
        final JButton up = new JButton("up");
        final JButton down = new JButton("down");
        final JButton stop = new JButton("stop");
        
        up.setEnabled(false);
        
        panel.add(this.display);
        
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().add(panel);
        
        this.frame.setVisible(true);
        
        final Agent agent = new Agent();
        new Thread(agent).start();
        
        up.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.upCount();
                up.setEnabled(false);
                down.setEnabled(true);
            }
        });
        
        down.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.downCount();
                down.setEnabled(false);
                up.setEnabled(true);
            }
        });
        
        stop.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.stopCount();
                stop.setEnabled(false);
                up.setEnabled(false);
                down.setEnabled(false);
            }
        });
    }
    
    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean up = true;
        private volatile int count;
        
        @Override
        public void run() {
            while(!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            ConcurrentGUI.this.display.setText(Integer.toString(Agent.this.count));
                        }
                    });
                    if(up) {
                        count++;
                    } else {
                      count--;
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e2) {
                    e2.printStackTrace();
                }
            }
        }
        
        public void upCount() {
            this.up = true;
        }
        
        public void downCount() {
            this.up = false;
        }
        
        public void stopCount() {
            this.stop = true;
        }
        
    }
}
