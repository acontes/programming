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
		layout.marginHeight = 20;
		layout.marginWidth = 20;
		shell.setLayout(layout);

		// label "URL"
		Label urlLabel = new Label(shell, SWT.NONE);
		final Combo urlCombo = new Combo(shell, SWT.BORDER);
		Label loginLabel = new Label(shell, SWT.NONE);
		final Combo login = new Combo(shell, SWT.BORDER);
		Label pwdLabel = new Label(shell, SWT.NONE);
		Text pwd = new Text(shell, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);

		urlLabel.setText("Url :");

		
		
		// text url
		FormData urlFormData = new FormData();
		urlFormData.top = new FormAttachment(0, -1);
		urlFormData.left = new FormAttachment(loginLabel, 5);
		urlFormData.right = new FormAttachment(100, -5);
		urlCombo.setLayoutData(urlFormData);
		
		loginLabel.setText("login :");
		FormData loginLabelFormData = new FormData();
		loginLabelFormData.top = new FormAttachment(login, 0, SWT.CENTER);
		loginLabel.setLayoutData(loginLabelFormData);
		
		FormData loginFormData = new FormData();
		loginFormData.top = new FormAttachment(urlCombo, 5);
		loginFormData.left = new FormAttachment(loginLabel, 5);
		loginFormData.right = new FormAttachment(45, 5);
		login.setLayoutData(loginFormData);

		pwdLabel.setText("password :");
		FormData pwdLabelFormData = new FormData();
		pwdLabelFormData.top = new FormAttachment(pwd, 0, SWT.CENTER);
		pwdLabelFormData.left = new FormAttachment(login, 5);
		pwdLabel.setLayoutData(pwdLabelFormData);
		
		FormData pwdFormData = new FormData();
		pwdFormData.top = new FormAttachment(urlCombo, 5);
		pwdFormData.left = new FormAttachment(pwdLabel, 5);
		pwdFormData.right = new FormAttachment(100, -5);
		pwd.setLayoutData(pwdFormData);
		//**********************************************************************
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("log as admin");
		Button check = new Button(shell, SWT.CHECK);
		
		FormData checkFormData = new FormData();
		checkFormData.top = new FormAttachment(label, 0, SWT.CENTER);
		checkFormData.left = new FormAttachment(50,-45);
		check.setLayoutData(checkFormData);
		
		FormData textFormData = new FormData();
		textFormData.top = new FormAttachment(login, 5);
		textFormData.left = new FormAttachment(check, 5);
		label.setLayoutData(textFormData);
		
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
		okFormData.top = new FormAttachment(label, 5);
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
		cancelFormData.top = new FormAttachment(label, 5);
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
