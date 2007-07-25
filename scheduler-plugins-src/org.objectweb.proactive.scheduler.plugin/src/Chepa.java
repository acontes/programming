import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Chepa {
//	Display display = new Display();
//	Shell shell = new Shell(display);
//
//	// the label used to display selected dir/file.
//	Label label;
//
//	Button buttonSelectDir;
//	Button buttonSelectFile;
//
//	String selectedDir;
//	String fileFilterPath = "F:/jdk1.5";
//
//	public Chepa() {
//		label = new Label(shell, SWT.BORDER | SWT.WRAP);
//		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
//		label.setText("Select a dir/file by clicking the buttons below.");
//
//		buttonSelectDir = new Button(shell, SWT.PUSH);
//		buttonSelectDir.setText("Select a directory");
//		buttonSelectDir.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event event) {
//				DirectoryDialog directoryDialog = new DirectoryDialog(shell);
//
//				directoryDialog.setFilterPath(selectedDir);
//				directoryDialog.setMessage("Please select a directory and click OK");
//
//				String dir = directoryDialog.open();
//				if (dir != null) {
//					label.setText("Selected dir: " + dir);
//					selectedDir = dir;
//				}
//			}
//		});
//
//		buttonSelectFile = new Button(shell, SWT.PUSH);
//		buttonSelectFile.setText("Select a file/multiple files");
//		buttonSelectFile.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event event) {
//				FileDialog fileDialog = new FileDialog(shell, SWT.MULTI);
//
//				fileDialog.setFilterPath(fileFilterPath);
//
//				fileDialog.setFilterExtensions(new String[] { "*.rtf", "*.html", "*.*" });
//				fileDialog.setFilterNames(new String[] { "Rich Text Format", "HTML Document", "Any" });
//
//				String firstFile = fileDialog.open();
//
//				if (firstFile != null) {
//					fileFilterPath = fileDialog.getFilterPath();
//					String[] selectedFiles = fileDialog.getFileNames();
//					StringBuffer sb = new StringBuffer("Selected files under dir "
//							+ fileDialog.getFilterPath() + ": \n");
//					for (int i = 0; i < selectedFiles.length; i++) {
//						sb.append(selectedFiles[i] + "\n");
//					}
//					label.setText(sb.toString());
//				}
//			}
//		});
//
//		label.setBounds(0, 0, 400, 60);
//		buttonSelectDir.setBounds(0, 65, 200, 30);
//		buttonSelectFile.setBounds(200, 65, 200, 30);
//
//		shell.pack();
//		shell.open();
//		// textUser.forceFocus();
//
//		// Set up the event loop.
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch()) {
//				// If no more entries in event queue
//				display.sleep();
//			}
//		}
//
//		display.dispose();
//	}

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		

		shell.setText("Connect to scheduler");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

		// label "URL"
		Label urlLabel = new Label(shell, SWT.NONE);
		urlLabel.setText("Url :");

		// text url
		final Combo urlCombo = new Combo(shell, SWT.BORDER);
		FormData urlFormData = new FormData();
		urlFormData.top = new FormAttachment(0, -1);
		urlFormData.left = new FormAttachment(urlLabel, 5);
		urlFormData.right = new FormAttachment(100, -5);
		urlCombo.setLayoutData(urlFormData);
		
		//**********************************************************************
		Label loginLabel = new Label(shell, SWT.NONE);
		loginLabel.setText("login :");
		FormData loginLabelFormData = new FormData();
		loginLabelFormData.top = new FormAttachment(urlCombo, 5);
		loginLabel.setLayoutData(loginLabelFormData);
		
		Text login = new Text(shell, SWT.SINGLE | SWT.BORDER);
		FormData loginFormData = new FormData();
		loginFormData.top = new FormAttachment(urlCombo, 5);
		loginFormData.left = new FormAttachment(loginLabel, 5);
		loginFormData.right = new FormAttachment(40, 5);
		login.setLayoutData(loginFormData);
		
		Label pwdLabel = new Label(shell, SWT.NONE);
		pwdLabel.setText("password :");
		FormData pwdLabelFormData = new FormData();
		pwdLabelFormData.top = new FormAttachment(urlCombo, 5);
		pwdLabelFormData.left = new FormAttachment(login, 5);
		pwdLabel.setLayoutData(pwdLabelFormData);
		
		Text pwd = new Text(shell, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		FormData pwdFormData = new FormData();
		pwdFormData.top = new FormAttachment(urlCombo, 5);
		pwdFormData.left = new FormAttachment(pwdLabel, 5);
		pwdFormData.right = new FormAttachment(100, -5);
		pwd.setLayoutData(pwdFormData);
		
		//**********************************************************************

		// button "OK"
		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText("OK");
		okButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				shell.close();
			}
		});
		FormData okFormData = new FormData();
		okFormData.top = new FormAttachment(login, 5);
		okFormData.left = new FormAttachment(25, 20);
		okFormData.right = new FormAttachment(50, -10);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// button "CANCEL"
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				shell.close();
			}
		});
		FormData cancelFormData = new FormData();
		cancelFormData.top = new FormAttachment(login, 5);
		cancelFormData.left = new FormAttachment(50, 10);
		cancelFormData.right = new FormAttachment(75, -20);
		cancelButton.setLayoutData(cancelFormData);

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

// System.out.println("***********");
// Calendar c = Calendar.getInstance();
// c.setTimeInMillis(System.currentTimeMillis());
// System.out.println(String.format("%1$tT %1$tD", c));
// Calendar c = new GregorianCalendar();
// Calendar.getInstance()
// String.format("%1$tH:%1$tM:%1$tS",c);
// System.out.println("***********");
// Display display = new Display();
// Shell shell = new Shell(display);
// FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
//
// fileDialog.setFilterExtensions(new String[] {"*.xml"});
//		
// //fileDialog.setFilterPath(fileFilterPath);
//
// //fileDialog.setFilterExtensions(new String[] { "*.rtf", "*.html", "*.*" });
// //fileDialog.setFilterNames(new String[] { "XML" });
//
// String firstFile = fileDialog.open();
//
// if (firstFile != null) {
// //fileFilterPath = fileDialog.getFilterPath();
// String[] selectedFiles = fileDialog.getFileNames();
// StringBuffer sb = new StringBuffer("Selected files under dir "
// + fileDialog.getFilterPath() + ": \n");
// for (int i = 0; i < selectedFiles.length; i++) {
// sb.append(selectedFiles[i] + "\n");
// }
// System.out.println(sb.toString());
// //label.setText(sb.toString());
// }
}
