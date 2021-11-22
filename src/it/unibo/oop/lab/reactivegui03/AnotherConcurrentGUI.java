package it.unibo.oop.lab.reactivegui03;

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

public class AnotherConcurrentGUI {
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JFrame frame = new JFrame();
    final JLabel display = new JLabel("0");
    final JButton up = new JButton("up");
    final JButton down = new JButton("down");
    final JButton stop = new JButton("stop");
    
    public AnotherConcurrentGUI() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.frame.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        
        final JPanel panel = new JPanel();
        
        this.up.setEnabled(false);
        
        panel.add(this.display);
        
        panel.add(this.up);
        panel.add(this.down);
        panel.add(this.stop);
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().add(panel);
        
        this.frame.setVisible(true);
        
        final Agent agent = new Agent();
        new Thread(agent).start();
        
        new Thread(new TenSecondsCounter(agent)).start();
        
        this.up.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.upCount();
                up.setEnabled(false);
                down.setEnabled(true);
            }
        });
        
        this.down.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.downCount();
                down.setEnabled(false);
                up.setEnabled(true);
            }
        });
        
        this.stop.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(final ActionEvent e) {
                stopCount(agent);
            }
        });
    }
    
    private void stopCount(final Agent agent) {
        agent.stopCount();
        this.stop.setEnabled(false);
        this.up.setEnabled(false);
        this.down.setEnabled(false);
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
                            AnotherConcurrentGUI.this.display.setText(Integer.toString(Agent.this.count));
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
    
    private class TenSecondsCounter implements Runnable {

        private final Agent agent;
        
        public TenSecondsCounter(final Agent agent) {
            this.agent = agent;
        }
        
        @Override
        public void run() {
            try {
                Thread.sleep(10_000);
                AnotherConcurrentGUI.this.stopCount(agent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
}
