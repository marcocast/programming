/*
* RMIHostDialog.java
*
* Created on 18 ao?t 2004, 13:49
* This class allow to seize host or ip machine to monitor and depth monitor control
*  It became in replacement of jOptionPane which does not allow multiple seized wigets
* thsi class is a singleton, so maxdepthvalue  monitor is keeped in it.    
*/

package org.objectweb.proactive.ic2d.gui.util;

/**
*
* @author  manu
*/
public class RMIHostDialog extends javax.swing.JDialog {
	private RMIHostDialog(){} //empty default constructor to avoid compiler to add one 
      /** Creates new form RMIHostDialog */
   private RMIHostDialog(java.awt.Frame parent, boolean modal) {
   	
       super(parent, modal);
       initComponents();
       setJTextFielddepth(defaultMaxDepth);
       setSize(450,220);
       
   }
      /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   private void initComponents() {

        getContentPane().setLayout(new java.awt.FlowLayout());

       jPanel1 = new javax.swing.JPanel();
       jPanel2 = new javax.swing.JPanel();
       jPanel3 = new javax.swing.JPanel();
       jPanel4 = new javax.swing.JPanel();
       jPanel5 = new javax.swing.JPanel();

       jTextFieldHostIp = new javax.swing.JTextField(30);
       jLabelHostIp = new javax.swing.JLabel();
       jTextFielddepth = new javax.swing.JTextField(2);
       jLabel2 = new javax.swing.JLabel();
       jButtonOK = new javax.swing.JButton();
       jButtonCancel = new javax.swing.JButton();
       jLabel3 = new javax.swing.JLabel();
       
       jButtonOK.setMnemonic(java.awt.event.KeyEvent.VK_ENTER);
       getRootPane().setDefaultButton(jButtonOK);
       setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
       setTitle("Adding host and assoc to monitor");
       setModal(true);
       setResizable(false);
  
       jLabelHostIp.setText("Please enter the name or the IP of the host to monitor :");
       jPanel1.add(jLabelHostIp);
       getContentPane().add(jPanel1);
       
       jTextFieldHostIp.setText("sdfsdfdsfsdfsd");
       jTextFieldHostIp.setSize(400,18);
       jPanel2.add( jTextFieldHostIp);
       getContentPane().add(jPanel2);

       jLabel2.setText("Hosts will be recursively searched up to a depth of :");
       jPanel3.add(jLabel2);
       jTextFielddepth.setText(defaultMaxDepth);
       jPanel3.add(jTextFielddepth);
       getContentPane().add(jPanel3);
       
       jLabel3.setText("You can change it there or from \"Menu Control, Set Depth Control\" ");
       jPanel4.add(jLabel3);
       getContentPane().add(jPanel4);
       
       jButtonOK.setText("OK");
       jButtonOK.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jButton1ActionPerformed(evt);
           }
       });

       jPanel5.add(jButtonOK);
       
       jButtonCancel.setText("Cancel");
       jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jButton2ActionPerformed(evt);
           }
       });

       jPanel5.add(jButtonCancel);
       getContentPane().add(jPanel5);
       
       pack();
            
        }

   private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
   	setButtonOK(false); 
       setVisible(false);
   }

   private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
   	setButtonOK(true); 
   	  setVisible(false);
   }
      /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
       new RMIHostDialog(new javax.swing.JFrame(), true).setVisible(true);
   }

   // Variables declaration - do not modify
   private static javax.swing.JPanel jPanel1;
   private static javax.swing.JPanel jPanel2;
   private static javax.swing.JPanel jPanel3;
   private static javax.swing.JPanel jPanel4;
   private static javax.swing.JPanel jPanel5;

   
   private javax.swing.JButton jButtonOK;
   private javax.swing.JButton jButtonCancel;
   private static  javax.swing.JLabel jLabelHostIp;
   private static  javax.swing.JLabel jLabel2;
   private static  javax.swing.JLabel jLabel3;
   private static  javax.swing.JTextField jTextFieldHostIp;
   private static javax.swing.JTextField jTextFielddepth;
   
   
   private boolean buttonOK=false;
   private static RMIHostDialog _singleton=null;
   static private String defaultMaxDepth="10";
   // End of variables declaration

   public String getJTextFielddepth() {
	return jTextFielddepth.getText();
}
public void setJTextFielddepth(String textFielddepth) {
	jTextFielddepth.setText(textFielddepth);
}
public String getJTextFieldHostIp() {
	return jTextFieldHostIp.getText();
}
public void setJTextFieldHostIp(String  textFieldHostIp) {
	jTextFieldHostIp.setText(textFieldHostIp);
}
public boolean isButtonOK() {
	return buttonOK;
}
public void setButtonOK(boolean buttonOK) {
	this.buttonOK = buttonOK;
}
// satic method for singleton pattern
static public RMIHostDialog showRMIHostDialog(javax.swing.JFrame parentComponent, String initialhostvalue){
	if (null==_singleton){
			_singleton = new RMIHostDialog(parentComponent, true);
		}
	jTextFieldHostIp.setText(initialhostvalue);
	_singleton.setLocationRelativeTo(parentComponent);
	_singleton.setVisible(true);
	return _singleton;
	}

public static void openSetDepthControlDialog(javax.swing.JFrame parentComponent){
	if (null==_singleton){
		_singleton = new RMIHostDialog(parentComponent, true);
	}
	
	Object result = javax.swing.JOptionPane.showInputDialog(parentComponent, // Component parentComponent,
            "Please enter the max depth control:", // Object message,
            "Set Depth Control", // String title,
            javax.swing.JOptionPane.PLAIN_MESSAGE, // int messageType,
            null, // Icon icon,
            null, // Object[] selectionValues,
            jTextFielddepth.getText() // Object initialSelectionValue)
        );
	
    if ((result == null) || (!(result instanceof String))) {
    	 return;
    }
    jTextFielddepth.setText((String)result);
	}	


   }
