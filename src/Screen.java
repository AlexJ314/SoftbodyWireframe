/** 
 * @file Screen.java
 * @brief Creates a window with the simulation display and control panel
 * @author Alex Johnson
 */

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.TextListener;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.InputEvent;

public class Screen extends JFrame {
  private Wireframe wireframe;
  private int width = Constants.frameWidth;
  private int height = Constants.frameHeight;
  private JPanel controls;
  private Display display;

  public Screen(Wireframe wireframe) {
    super();
    this.setBackground(Color.WHITE);
    this.setSize(this.width, this.height);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.wireframe = wireframe;
    this.addDisplay();
    this.addControls();
    this.setVisible(true);
  }

  private void addDisplay() {
    this.display = new Display(this.wireframe);
    this.getContentPane().add(this.display);
    Screen s = this;
    this.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        super.componentResized(e);
        synchronized(Constants.lock) {
          s.setWidth(s.getSize().width);
          s.setHeight(s.getSize().height);
          s.display.setWidth(s.width);
          s.display.setHeight(s.height-(Constants.frameHeight-Constants.panelHeight));
          s.controls.setSize(s.width, Constants.frameHeight-Constants.panelHeight);
        }
      }
    });
  }

  private void addControls() {
    JRadioButton lockPointButton = new JRadioButton("Lock point");
    DisplayMouse dp = new DisplayMouse(this.display) {
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        synchronized(Constants.lock) {
          lockPointButton.setSelected(this.getLockPointToggle());
        }
        this.getDisplay().setFocusable(true);
        this.getDisplay().requestFocusInWindow();
      }
    };
    this.addMouseMotionListener(dp);
    this.addMouseListener(dp);
    this.addMouseWheelListener(dp);

    this.controls = new JPanel();
    synchronized(Constants.lock) {
      this.controls.setSize(this.width, this.height-Constants.panelHeight);
    }
    this.controls.setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
    this.controls.setBackground(Color.WHITE);
    this.controls.setLayout(new GridBagLayout());

    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.BOTH;
    gc.anchor = GridBagConstraints.CENTER;
    gc.gridwidth = 1;
    gc.gridheight = 1;

    JRadioButton addPointButton = new JRadioButton("Add point");
    addPointButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getDisplayMouse().addPointToggle();
        }
      }
    });
    gc.gridx = 0;
    gc.gridy = 0;
    this.controls.add(addPointButton, gc);

    JButton removePointButton = new JButton("Remove point");
    removePointButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getDisplayMouse().removePoint();
          lockPointButton.setSelected(this.getDisplayMouse().getLockPointToggle());
        }
      }
    });
    gc.gridx = 0;
    gc.gridy = 1;
    this.controls.add(removePointButton, gc);

    JButton mergePointButton = new JButton("Merge points");
    mergePointButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getDisplayMouse().mergePoints();
          lockPointButton.setSelected(this.getDisplayMouse().getLockPointToggle());
        }
      }
    });
    gc.gridx = 0;
    gc.gridy = 2;
    this.controls.add(mergePointButton, gc);

    TextField edgeLengthField = new TextField("Edge length (1)");

    JButton addEdgeButton = new JButton("Add edge");
    addEdgeButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          edgeLengthField.setText(this.getDisplayMouse().setEdgeLength(edgeLengthField.getText()));
          this.getDisplayMouse().addEdge();
        }
      }
    });
    gc.gridx = 1;
    gc.gridy = 0;
    this.controls.add(addEdgeButton, gc);

    JButton removeEdgeButton = new JButton("Remove edge");
    removeEdgeButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getDisplayMouse().removeEdge();
        }
      }
    });
    gc.gridx = 1;
    gc.gridy = 1;
    this.controls.add(removeEdgeButton, gc);

    JButton bisectButton = new JButton("Bisect");
    bisectButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          edgeLengthField.setText(this.getDisplayMouse().setEdgeLength(edgeLengthField.getText()));
          this.getDisplayMouse().bisect();
        }
      }
    });
    gc.gridx = 1;
    gc.gridy = 2;
    this.controls.add(bisectButton, gc);

    TextField edgeCountField = new TextField("Edge count (3)");

    JButton addPolygonButton = new JButton("Add polygon");
    addPolygonButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          edgeCountField.setText(this.getDisplayMouse().setEdgeCount(edgeCountField.getText()));
          edgeLengthField.setText(this.getDisplayMouse().setEdgeLength(edgeLengthField.getText()));
          if (this.getDisplayMouse().getLastClickEvent() != null) {
            this.getDisplayMouse().addPolygon(this.getDisplayMouse().getLastClickEvent().getX(),
              this.getDisplayMouse().getLastClickEvent().getY());
          } else {
            this.getDisplayMouse().addPolygon((int) this.getDisplay().getWidth()/2,
              (int) this.getDisplay().getHeight()/2);
          }
        }
      }
    });
    gc.gridx = 2;
    gc.gridy = 0;
    this.controls.add(addPolygonButton, gc);

    edgeCountField.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          edgeCountField.setText(this.getDisplayMouse().setEdgeCount(edgeCountField.getText()));
        }
      }
    });
    gc.gridx = 2;
    gc.gridy = 1;
    this.controls.add(edgeCountField, gc);

    edgeLengthField.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          edgeLengthField.setText(this.getDisplayMouse().setEdgeLength(edgeLengthField.getText()));
        }
      }
    });
    gc.gridx = 2;
    gc.gridy = 2;
    this.controls.add(edgeLengthField, gc);

    JRadioButton lockXButton = new JRadioButton("Lock x");
    JRadioButton lockYButton = new JRadioButton("Lock y");
    JRadioButton lockZButton = new JRadioButton("Lock z");

    lockXButton.addActionListener(new DisplayActions(this.display) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          Vector3 v = new Vector3(1, 0, 0);
          if (v.equals(this.getDisplay().getLock())) {
            this.getDisplay().setLock(null);
            lockXButton.setSelected(false);
          } else {
            this.getDisplay().setLock(v);
            lockXButton.setSelected(true);
            lockYButton.setSelected(false);
            lockZButton.setSelected(false);
          }
        }
      }
    });
    gc.gridx = 3;
    gc.gridy = 0;
    this.controls.add(lockXButton, gc);

    lockYButton.addActionListener(new DisplayActions(this.display) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          Vector3 v = new Vector3(0, 1, 0);
          if (v.equals(this.getDisplay().getLock())) {
            this.getDisplay().setLock(null);
            lockYButton.setSelected(false);
          } else {
            this.getDisplay().setLock(v);
            lockXButton.setSelected(false);
            lockYButton.setSelected(true);
            lockZButton.setSelected(false);
          }
        }
      }
    });
    gc.gridx = 3;
    gc.gridy = 1;
    this.controls.add(lockYButton, gc);

    lockZButton.addActionListener(new DisplayActions(this.display) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          Vector3 v = new Vector3(0, 0, 1);
          if (v.equals(this.getDisplay().getLock())) {
            this.getDisplay().setLock(null);
            lockZButton.setSelected(false);
          } else {
            this.getDisplay().setLock(v);
            lockXButton.setSelected(false);
            lockYButton.setSelected(false);
            lockZButton.setSelected(true);
          }
        }
      }
    });
    gc.gridx = 3;
    gc.gridy = 2;
    this.controls.add(lockZButton, gc);

    JButton zoomInButton = new JButton("Zoom in");
    zoomInButton.addActionListener(new DisplayActions(this.display) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getDisplay().zoomIn();
        }
      }
    });
    gc.gridx = 4;
    gc.gridy = 0;
    this.controls.add(zoomInButton, gc);

    JButton zoomOutButton = new JButton("Zoom out");
    zoomOutButton.addActionListener(new DisplayActions(this.display) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getDisplay().zoomOut();
        }
      }
    });
    gc.gridx = 4;
    gc.gridy = 1;
    this.controls.add(zoomOutButton, gc);

    TextField printField = new TextField(this.wireframe.getPrintFile());
    printField.addActionListener(new WireframeActions(this.wireframe) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          printField.setText(this.getWireframe().setPrintFile(printField.getText()));
        }
      }
    });
    gc.gridx = 4;
    gc.gridy = 2;
    this.controls.add(printField, gc);

    JButton jiggleButton = new JButton("Jiggle");
    jiggleButton.addActionListener(new WireframeActions(this.wireframe) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getWireframe().jiggle();
        }
      }
    });
    gc.gridx = 5;
    gc.gridy = 0;
    this.controls.add(jiggleButton, gc);

    JRadioButton freezeButton = new JRadioButton("Freeze");
    freezeButton.addActionListener(new WireframeActions(this.wireframe) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getWireframe().freeze();
        }
      }
    });
    gc.gridx = 5;
    gc.gridy = 1;
    this.controls.add(freezeButton, gc);

    JButton printButton = new JButton("Print");
    printButton.addActionListener(new WireframeActions(this.wireframe) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          printField.setText(this.getWireframe().setPrintFile(printField.getText()));
          this.getWireframe().print();
        }
      }
    });
    gc.gridx = 5;
    gc.gridy = 2;
    this.controls.add(printButton, gc);

    JRadioButton repulseButton = new JRadioButton("Repulse");
    repulseButton.setSelected(Constants.doFullRepulsive);
    repulseButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getDisplay().getWireframe().doFullRepulsiveToggle();
        }
      }
    });
    gc.gridx = 6;
    gc.gridy = 0;
    this.controls.add(repulseButton, gc);

    lockPointButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getDisplayMouse().lockPointToggle();
          lockPointButton.setSelected(this.getDisplayMouse().getLockPointToggle());
        }
      }
    });
    gc.gridx = 6;
    gc.gridy = 1;
    this.controls.add(lockPointButton, gc);

    JRadioButton stressButton = new JRadioButton("Show stress");
    stressButton.addActionListener(new DisplayActions(dp) {
      public void actionPerformed(ActionEvent e) {
        synchronized(Constants.lock) {
          this.getDisplay().showStressToggle();
        }
      }
    });
    gc.gridx = 6;
    gc.gridy = 2;
    this.controls.add(stressButton, gc);

    this.display.setFocusable(true);
    this.display.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        synchronized(Constants.lock) {
          int k = e.getExtendedKeyCode();
          int m = e.getModifiersEx();
          if (k == KeyEvent.VK_ESCAPE) {
            dp.deselect();
          } else if (m == 0 && k == KeyEvent.VK_DELETE) {
            dp.removePoint();
            lockPointButton.setSelected(dp.getLockPointToggle());
          } else if (m == 0 && k == KeyEvent.VK_BACK_SPACE) {
            dp.removeEdge();
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_A) {
            dp.addPointToggle();
            addPointButton.setSelected(!addPointButton.isSelected());
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_B) {
            edgeLengthField.setText(dp.setEdgeLength(edgeLengthField.getText()));
            dp.bisect();
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_E) {
            edgeLengthField.setText(dp.setEdgeLength(edgeLengthField.getText()));
            dp.addEdge();
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_F) {
            dp.getDisplay().getWireframe().freeze();
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_G) {
            edgeCountField.setText(dp.setEdgeCount(edgeCountField.getText()));
            edgeLengthField.setText(dp.setEdgeLength(edgeLengthField.getText()));
            if (dp.getLastClickEvent() != null) {
              dp.addPolygon(dp.getLastClickEvent().getX(),
                dp.getLastClickEvent().getY());
            } else {
              dp.addPolygon((int) dp.getDisplay().getWidth()/2,
                (int) dp.getDisplay().getHeight()/2);
            }
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_L) {
            dp.lockPointToggle();
            lockPointButton.setSelected(dp.getLockPointToggle());
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_M) {
            dp.mergePoints();
            lockPointButton.setSelected(dp.getLockPointToggle());
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_P) {
            printField.setText(dp.getDisplay().getWireframe().setPrintFile(printField.getText()));
            dp.getDisplay().getWireframe().print();
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_X) {
            Vector3 v = new Vector3(1, 0, 0);
            if (v.equals(dp.getDisplay().getLock())) {
              dp.getDisplay().setLock(null);
              lockXButton.setSelected(false);
            } else {
              dp.getDisplay().setLock(v);
              lockXButton.setSelected(true);
              lockYButton.setSelected(false);
              lockZButton.setSelected(false);
            }
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_Y) {
            Vector3 v = new Vector3(0, 1, 0);
            if (v.equals(dp.getDisplay().getLock())) {
              dp.getDisplay().setLock(null);
              lockYButton.setSelected(false);
            } else {
              dp.getDisplay().setLock(v);
              lockXButton.setSelected(false);
              lockYButton.setSelected(true);
              lockZButton.setSelected(false);
            }
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_Z) {
            Vector3 v = new Vector3(0, 0, 1);
            if (v.equals(dp.getDisplay().getLock())) {
              dp.getDisplay().setLock(null);
              lockZButton.setSelected(false);
            } else {
              dp.getDisplay().setLock(v);
              lockXButton.setSelected(false);
              lockYButton.setSelected(false);
              lockZButton.setSelected(true);
            }
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_SPACE) {
            dp.getDisplay().getWireframe().doFullRepulsiveToggle();
            repulseButton.setSelected(!repulseButton.isSelected());
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_EQUALS) {
            dp.getDisplay().zoomIn();
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_MINUS) {
            dp.getDisplay().zoomOut();
          } else if (m == 0 && k == KeyEvent.VK_UP) {
            dp.getDisplay().panBy(0, -10);
          } else if (m == 0 && k == KeyEvent.VK_DOWN) {
            dp.getDisplay().panBy(0, 10);
          } else if (m == 0 && k == KeyEvent.VK_LEFT) {
            dp.getDisplay().panBy(-10, 0);
          } else if (m == 0 && k == KeyEvent.VK_RIGHT) {
            dp.getDisplay().panBy(10, 0);
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_R) {
            dp.reset();
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_C) {
            dp.copy();
          } else if (m == InputEvent.CTRL_DOWN_MASK && k == KeyEvent.VK_V) {
            dp.paste();
          }
        }
      }
    });

    this.getContentPane().add(this.controls, BorderLayout.SOUTH);
  }

  public void tick() {
    this.wireframe.tick(); 
    this.repaint();
  }

  public void setWidth(int w) {
    if (w > 0) {
      this.width = w;
    }
  }

  public void setHeight(int h) {
    if (h > 0) {
      this.height = h;
    }
  }
}
