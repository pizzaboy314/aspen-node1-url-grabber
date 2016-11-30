package classes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Worker {

	private static JFrame resultFrame;
	private static JTextArea resultText;
	private static JFileChooser fc;
	private static File resultFile;
	private static String resultString;
	
	private static List<SISserver> districtNodes;

	public static void main(String[] args) {
		resultWindow();
		
		districtNodes = new ArrayList<SISserver>();

		String coolerURL = "http://cooler.fsc.follett.com/utilities/aspen/aspenprodsites.php";
		
		System.out.println();
		System.out.println();
		
//		albumURL = (String) JOptionPane.showInputDialog(null,
//				"Enter Apple Music album url: \n(Example: https://itunes.apple.com/us/album/abbey-road/id401186200)", "Enter Apple Music album url",
//				JOptionPane.PLAIN_MESSAGE, null, null, null);
//
//		if (albumURL == null || albumURL.equals("")) {
//			System.exit(0);
//		}

		parseHTML(coolerURL);
		Collections.sort(districtNodes);
		printAllURLs();
		
		resultString = "insert node url here";
		resultText.setText(resultString);
		resultFrame.setVisible(true);
		resultFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		System.out.println();

	}
	
	public static void printAllURLs (){
		for(SISserver sis : districtNodes){
			if(sis.getServerstring().contains("wrxx")){
				System.out.println("http://"+ sis.getServerstring()+".fsc.follett.com:8080/aspen/logon.do?deploymentId=" + sis.getDeploymentID());
			} else {
				System.out.println("http://"+ sis.getServerstring()+".fss.follett.com:8080/aspen/logon.do?deploymentId=" + sis.getDeploymentID());
			}

		}
	}
	
	public static void parseHTML(String input) {
		JFrame frame = new JFrame("Parsing cooler page...");
		frame.setPreferredSize(new Dimension(300, 120));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel label1 = new JLabel();
		label1.setPreferredSize(new Dimension(200, 40));
		label1.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label1);

		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		String url = input;

		try {
			URL source = null;
			boolean valid = true;
			try {
				source = new URL(url);
			} catch (MalformedURLException e) {
				valid = false;
			}
			while (valid == false) {
				valid = true;
				url = (String) JOptionPane.showInputDialog(null, "Malformed URL format. Are you sure you copied the entire URL?\n" + "Try again:",
						"Provide URL", JOptionPane.PLAIN_MESSAGE, null, null, null);
				try {
					source = new URL(url);
				} catch (MalformedURLException e) {
					valid = false;
				}
			}
			
			Document doc = Jsoup.connect(url).get();
			Elements tableElements = doc.select("table");

			Elements tableRowElements = tableElements.select(":not(thead) tr");

			for (int i = 0; i < tableRowElements.size(); i++) {
				Element row = tableRowElements.get(i);
				Elements rowItems = row.select("td");
				
				SISserver sis = new SISserver(rowItems.get(0).text(), rowItems.get(1).text());
				
				if(!sis.getDeploymentID().contains("aspen") && !sis.getDeploymentID().contains("test") && !sis.getDeploymentID().contains("demo") && !sis.getDeploymentID().contains("Site Name")){
					districtNodes.add(sis);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static void resultWindow() {
		resultFrame = new JFrame("Results");
		File resultPath = new File(System.getProperty("user.dir"));
		fc = new JFileChooser(resultPath);

		FileFilter filter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		int x = (int) (frameSize.width / 2);
		int y = (int) (frameSize.height / 2);
		resultFrame.setBounds(x, y, 600, frameSize.height);

		resultText = new JTextArea();
		resultText.setText("");
		resultText.setEditable(false);

		JButton saveFile = new JButton("Save Results");
		saveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					String filepath = f.getAbsolutePath();
					String filename = f.getName();

					if (!filename.contains(".txt")) {
						resultFile = new File(filepath + ".txt");
					} else {
						resultFile = f;
					}

					try {
						Files.write(Paths.get(resultFile.getAbsolutePath()), resultString.getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JPanel controls = new JPanel();
		controls.setLayout(new FlowLayout());
		controls.add(saveFile);

		resultFrame.getContentPane().add(new JScrollPane(resultText), BorderLayout.CENTER);
		resultFrame.getContentPane().add(controls, BorderLayout.SOUTH);
	}

}
