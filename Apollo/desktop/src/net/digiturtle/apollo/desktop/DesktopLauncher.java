package net.digiturtle.apollo.desktop;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.ApolloSettings;

public class DesktopLauncher {
	public static void main (String[] arg) throws InterruptedException {
		Apollo.productKey = findProductKey();
		
		if (Apollo.productKey == null) {
			getProductKey();
		}
		
		while (Apollo.productKey == null) {
			// Wait
			Thread.sleep(200);
		}
		
		saveProductKey();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Apollo " + ApolloSettings.VERSION;
		config.width = 1278;
		config.height = 720;
		new LwjglApplication(new Apollo(), config);
	}
	
	private static String findProductKey () {
		File file = new File("ProductKey.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String key = reader.readLine();
			return key;
		} catch (IOException e) {
			return null; // Key not found
		}
	}
	
	private static void saveProductKey () {
		File file = new File("ProductKey.txt");
		if (!file.exists()) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(Apollo.productKey);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	private static JTextField productKeyInput;
	private static JButton launchButton;
	private static void getProductKey () {
		final JFrame jframe = new JFrame();
		JPanel contentPanel = new JPanel();
		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		contentPanel.setBorder(padding);
		jframe.setContentPane(contentPanel);
		jframe.getContentPane().setLayout(new GridLayout(1, 3));
		jframe.getContentPane().add(new JLabel("Product Key"));
		productKeyInput = new JTextField("");
		jframe.getContentPane().add(productKeyInput);
		launchButton = new JButton("Launch");
		launchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Apollo.productKey = productKeyInput.getText();
				jframe.dispatchEvent(new WindowEvent(jframe, WindowEvent.WINDOW_CLOSING));
			}
			
		});
		jframe.getContentPane().add(launchButton);
		jframe.setTitle("Apollo Launcher");
		jframe.setSize(400, 80);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		jframe.setLocation(dim.width/2-jframe.getSize().width/2, dim.height/2-jframe.getSize().height/2);
		jframe.setVisible(true);
	}
}
