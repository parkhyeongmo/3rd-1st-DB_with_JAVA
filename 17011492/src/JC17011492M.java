import java.io.*;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JC17011492M extends JFrame {
	
   Connection con;
   
   String table_name[] = {"Doctors", "Nurses", "Patients", "Treatments", "Charts"};
   
   JTextArea printArea = new JTextArea(15,78);
   JTextField writeArea = new JTextField(65);
   JComboBox<String> combo = new JComboBox<String>(table_name);
   
   public JC17011492M() {
     String Driver="";
     String url="jdbc:mysql://localhost:3306/hospital?&serverTimezone=Asia/Seoul"; 
     String userid="hospital";
     String pwd="hospital";
     
     try {
       Class.forName("com.mysql.cj.jdbc.Driver");   
       System.out.println("����̹� �ε� ����");
       printArea.append("����̹� �ε� ����\n");
     } catch(ClassNotFoundException e) {
         e.printStackTrace();
      }
     
     try {
       System.out.println("�����ͺ��̽� ���� �غ�...");
       printArea.append("�����ͺ��̽� ���� �غ�...\n");
       con=DriverManager.getConnection(url, userid, pwd);
       System.out.println("�����ͺ��̽� ���� ����");
       printArea.append("�����ͺ��̽� ���� ����\n");
       printArea.append("������ �Է��� ��� �Է°��� /�� ������ �Է����ֽʽÿ�.\n");
       printArea.append("Doctors ���ý� :doc_id/major_treat/doc_name/doc_gen/doc_phone/doc_email/doc_position\n");
       printArea.append("Nurses ���ý� :nur_id/major_job/nur_name/nur_gen/nur_phone/nur_email/nur_position\n");
       printArea.append("Patients ���ý� :pat_id/nur_id/doc_id/pat_name/pat_gen/pat_jumin/pat_addr/pat_phone/pat_email/pat_job\n");
       printArea.append("Treatments ���ý� :treat_id/pat_id/doc_id/treat_contents/treat_date (���� ��¥�� yyyy-mm-dd �������� �Է����ֽʽÿ�.)\n");
       printArea.append("Charts ���ý� :chart_id/treat_id/doc_id/pat_id/nur_id/chart_contents\n");
     } catch(SQLException e) {
         e.printStackTrace();
       }
     
     setTitle("17011492/������");
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     Container c = getContentPane();
     c.setLayout(new FlowLayout());
			
     JButton btn1 = new JButton("�ʱ�ȭ");
     JButton btn2 = new JButton("�Է�");
     JButton btn3 = new JButton("�˻�-������̺�");
     JButton btn4 = new JButton("�˻�1");
     JButton btn5 = new JButton("�˻�2");
     JButton btn6 = new JButton("�˻�3");
		
     JLabel inform1 = new JLabel("�Է�");
     JLabel inform2 = new JLabel("���");
     
     JPanel inPanel = new JPanel();
     JPanel outPanel = new JPanel();
     
     inPanel.add(inform1);
     inPanel.add(combo);
     inPanel.add(writeArea);
     
     outPanel.add(inform2);
     outPanel.add(printArea);
	
     btn1.addActionListener(new ActionListener() { // �ʱ�ȭ
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlClear();    		 
    	 }	
     });
     
     btn2.addActionListener(new ActionListener() { // �Է�
    	 @Override
    	 public void actionPerformed(ActionEvent e) {		    		 
    		 sqlAdd();
    	 }	
     });
     
     btn3.addActionListener(new ActionListener() { // �˻� - ������̺�
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlRun();    		 
    	 }	
     });
		
     btn4.addActionListener(new ActionListener() { // �˻�1
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlSearch1();    		 
    	 }	
     });
     
     btn5.addActionListener(new ActionListener() { // �˻�2
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlSearch2();
    	 }	
     });
     
     btn6.addActionListener(new ActionListener() { // �˻�3
    	 @Override
    	 public void actionPerformed(ActionEvent e) {			
    		 sqlSearch3();
    	 }	
     });
     
		
     c.add(btn1);
     c.add(btn2);
     c.add(btn3);
     c.add(btn4);
     c.add(btn5);
     c.add(btn6);
		     
     c.add(inPanel);
     c.add(outPanel);
     
     c.add(new JScrollPane(printArea));
		
     setSize(900, 450);
     setVisible(true);
     
   }
// ���� ��ü con�� DriverManager.getConnection �Լ��� �����Ѵ�. 
// ������ �����ϸ� "�����ͺ��̽� ���� ����"�� ����ϵ��� �Ѵ�.  
// ���ڿ� query�� ������ SQL ���� �Է��Ѵ�.
   
   private void sqlClear() { // �ʱ�ȭ ��� �޼ҵ�
	   
	   printArea.setText("�ʱ�ȭ");
	   
	   Statement stmt = null;
	   
	   try {
			System.out.println("Clear");
			stmt = con.createStatement(); // SQL�� ó���� Statement ��ü ����
			
			stmt.executeUpdate("drop table charts");
			stmt.executeUpdate("drop table treatments");
			stmt.executeUpdate("drop table patients;");
			stmt.executeUpdate("drop table doctors;");
			stmt.executeUpdate("drop table nurses;");

			stmt.executeUpdate("CREATE TABLE Doctors (\r\n"
					+ "  doc_id INTEGER NOT NULL,\r\n"
					+ "  major_treat VARCHAR(25) NOT NULL,\r\n"
					+ "  doc_name VARCHAR(20) NOT NULL,\r\n"
					+ "  doc_gen VARCHAR(1) NOT NULL,\r\n"
					+ "  doc_phone VARCHAR(15) NULL,\r\n"
					+ "  doc_email VARCHAR(50) UNIQUE,\r\n"
					+ "  doc_position VARCHAR(20) NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("CREATE TABLE Nurses (\r\n"
					+ "  nur_id INTEGER NOT NULL,\r\n"
					+ "  major_job VARCHAR(25) NOT NULL,\r\n"
					+ "  nur_name VARCHAR(20) NOT NULL,\r\n"
					+ "  nur_gen char(1) NOT NULL,\r\n"
					+ "  nur_phone VARCHAR(15) NULL,\r\n"
					+ "  nur_email VARCHAR(50) UNIQUE,\r\n"
					+ "  nur_position VARCHAR(20) NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("CREATE TABLE Patients (\r\n"
					+ "  pat_id INTEGER NOT NULL,\r\n"
					+ "  nur_id INTEGER NOT NULL,\r\n"
					+ "  doc_id INTEGER NOT NULL,\r\n"
					+ "  pat_name VARCHAR(20) NOT NULL,\r\n"
					+ "  pat_gen VARCHAR(1) NOT NULL,\r\n"
					+ "  pat_jumin VARCHAR(14) NOT NULL,\r\n"
					+ "  pat_addr VARCHAR(100) NOT NULL,\r\n"
					+ "  pat_phone VARCHAR(15) NULL,\r\n"
					+ "  pat_email VARCHAR(50) UNIQUE,\r\n"
					+ "  pat_job VARCHAR(20) NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("CREATE TABLE Treatments (\r\n"
					+ "  treat_id INTEGER NOT NULL,\r\n"
					+ "  pat_id INTEGER NOT NULL,\r\n"
					+ "  doc_id INTEGER NOT NULL,\r\n"
					+ "  treat_contents VARCHAR(1000) NOT NULL,\r\n"
					+ "  treat_date DATE NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("CREATE TABLE Charts (\r\n"
					+ "  chart_id VARCHAR(20) NOT NULL,\r\n"
					+ "  treat_id INTEGER NOT NULL,\r\n"
					+ "  doc_id INTEGER NOT NULL,\r\n"
					+ "  pat_id INTEGER NOT NULL,\r\n"
					+ "  nur_id INTEGER NOT NULL,\r\n"
					+ "  chart_contents VARCHAR(1000) NOT NULL\r\n"
					+ ");");
			
			stmt.executeUpdate("INSERT INTO doctors VALUES (980312, '�Ҿư�', '������', 'M', '010-333-1340', 'ltj@hanbit.com', '����');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (000601, '����', '�ȼ���', 'M', '011-222-0987', 'ask@hanbit.com', '����');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (001208, '�ܰ�', '�����', 'M', '010-333-8743', 'kmj@hanbit.com', '����');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (020403, '�Ǻΰ�', '���¼�', 'M', '019-777-3764', 'lts@hanbit.com', '����');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (050900, '�Ҿư�', '�迬��', 'F', '010-555-3746', 'kya@hanbit.com', '������');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (050101, '����', '������', 'M', '011-222-7643', 'cth@hanbit.com', '������');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (062019, '�Ҿư�', '������', 'F', '010-999-1265', 'jjh@hanbit.com', '������');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (070576, '�Ǻΰ�', 'ȫ�浿', 'M', '016-333-7263', 'hgd@hanbit.com', '������');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (080543, '��缱��', '���缮', 'M', '010-222-1263', 'yjs@hanbit.com', '����');");
			stmt.executeUpdate("INSERT INTO doctors VALUES (091001, '�ܰ�', '�躴��', 'M', '010-555-3542', 'kbm@hanbit.com', '������');");
			
			stmt.executeUpdate("INSERT INTO nurses VALUES (050302, '�Ҿư�', '������', 'F', '010-555-8751', 'key@hanbit.com', '����ȣ��');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (050021, '����', '������', 'F', '016-333-8745', 'ysa@hanbit.com', '����ȣ��');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (040089, '�Ǻΰ�', '������', 'M', '010-666-7646', 'sjw@hanbit.com', '����');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (070605, '��缱��', '����ȭ', 'F', '010-333-4588', 'yjh@hanbit.com', '����');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (070804, '����', '���ϳ�', 'F', '010-222-1340', 'nhn@hanbit.com', '����');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (071018, '�Ҿư�', '��ȭ��', 'F', '019-888-4116', 'khk@hanbit.com', '����');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (100356, '�Ҿư�', '�̼���', 'M', '010-777-1234', 'lsy@hanbit.com', '��ȣ��');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (104145, '�ܰ�', '����', 'M', '010-999-8520', 'kh@hanbit.com', '��ȣ��');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (120309, '�Ǻΰ�', '�ڼ���', 'M', '010-777-4996', 'psw@hanbit.com', '��ȣ��');");
			stmt.executeUpdate("INSERT INTO nurses VALUES (130211, '�ܰ�', '�̼���', 'F', '010-222-3214', 'lsy2@hanbit.com', '��ȣ��');");
			
			stmt.executeUpdate("INSERT INTO patients VALUES (2345, 050302, 980312, '�Ȼ��', 'M', 232345, '����', '010-555-7845', 'ask@ab.com', 'ȸ���');");
			stmt.executeUpdate("INSERT INTO patients VALUES (3545, 040089, 020403, '�輺��', 'M', 543545, '����', '010-333-7812', 'ksn@bb.com', '�ڿ���');");
			stmt.executeUpdate("INSERT INTO patients VALUES (3424, 070605, 080543, '������', 'M', 433424, '�λ�', '010-888-4859', 'ljj@ab.com', 'ȸ���');");
			stmt.executeUpdate("INSERT INTO patients VALUES (7675, 100356, 050900, '�ֱ���', 'M', 677675, '����', '010-222-4847', 'cks@cc.com', 'ȸ���');");
			stmt.executeUpdate("INSERT INTO patients VALUES (4533, 070804, 000601, '���Ѱ�', 'M', 744533, '����', '010-777-9630', 'jhk@ab.com', '����');");
			stmt.executeUpdate("INSERT INTO patients VALUES (5546, 120309, 070576, '������', 'M', 765546, '�뱸', '016-777-0214', 'ywh@cc.com', '�ڿ���');");
			stmt.executeUpdate("INSERT INTO patients VALUES (4543, 070804, 050101, '������', 'M', 454543, '�λ�', '010-555-4187', 'cjj@bb.com', 'ȸ���');");
			stmt.executeUpdate("INSERT INTO patients VALUES (9768, 130211, 091001, '������', 'F', 119768, '����', '010-888-3675', 'ljh@ab.com', '����');");
			stmt.executeUpdate("INSERT INTO patients VALUES (4234, 130211, 091001, '������', 'F', 234234, '����', '010-999-6541', 'onm@cc.com', '�л�');");
			stmt.executeUpdate("INSERT INTO patients VALUES (7643, 071018, 062019, '�ۼ���', 'M', 987643, '����', '010-222-5874', 'ssm@bb.com', '�л�');");
			
			stmt.executeUpdate("INSERT INTO treatments VALUES (130516023, 2345, 980312, '����, ����', STR_TO_DATE('2013-05-16','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (130628100, 3545, 020403, '�Ǻ� Ʈ���� ġ��', STR_TO_DATE('2013-06-28','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (131205056, 3424, 080543, '�� ��ũ�� MRI �Կ�', STR_TO_DATE('2013-12-05','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (131218024, 7675, 050900, '���̿�', STR_TO_DATE('2013-12-18','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (131224012, 4533, 000601, '�忰', STR_TO_DATE('2013-12-24','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140103001, 5546, 070576, '���帧 ġ��', STR_TO_DATE('2014-01-03','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140109026, 4543, 050101, '����', STR_TO_DATE('2014-01-09','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140226102, 9768, 091001, 'ȭ��ġ��', STR_TO_DATE('2014-02-26','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140303003, 4234, 091001, '������ �ܻ�ġ��', STR_TO_DATE('2014-03-03','%Y-%m-%d'));");
			stmt.executeUpdate("INSERT INTO treatments VALUES (140308087, 7643, 062019, '�忰', STR_TO_DATE('2014-03-08','%Y-%m-%d'));");
			
			stmt.executeUpdate("INSERT INTO charts VALUES ('PD13572410', 130516023, 980312, 2345, 050302, '����, ����� ó��');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('DM11389132', 130628100, 020403, 3545, 040089, '�Ǻξ� ó��');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('RD10023842', 131205056, 080543, 3424, 070605, '�� ��ũ �ǽ�, �߰� �˻� �ʿ�');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('PD13581241', 131218024, 050900, 7675, 100356, '���ݰ��� �߰� �˻� �ʿ�');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('IM12557901', 131224012, 000601, 4533, 070804, '����� ó��');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('DM11400021', 140103001, 070576, 5546, 120309, '���帧 ġ���� ó��');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('IM12708224', 140109026, 050101, 4543, 070804, '���� �ɰ�, �߰� �˻� �� ���� ����');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('GS17223681', 140226102, 091001, 9768, 130211, 'ȭ��� ó�� �� ����ġ��');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('GS17264430', 140303003, 091001, 4234, 130211, '�߰� �������� �ʿ���');");
			stmt.executeUpdate("INSERT INTO charts VALUES ('PD13664611', 140308087, 062019, 7643, 071018, '�忰�� ó��');");
			
			printArea.append(" ����");
			
		} catch (SQLException e) {
			System.out.println("SQL ���� ����");
			printArea.append(" ����");
			   
		}
	   
   }
   
   private void sqlAdd() { // �Է� ��� �޼ҵ�
	   
	   printArea.setText("�Է�");
	   
	   String input = writeArea.getText();
	   String[] factor = input.split("/");
	   String selected_name = combo.getSelectedItem().toString();
	   	  
	   writeArea.setText("");
	   
	   Statement stmt = null;
	   
	   try {		   
			stmt = con.createStatement(); // SQL�� ó���� Statement ��ü ����
			
			if (selected_name.equals("Doctors")){		

				stmt.executeUpdate("insert into doctors values(" + Integer.parseInt(factor[0]) + ",'" + factor[1] + "','" + factor[2] + "','" + factor[3]+ "' ,'" + factor[4] + "','" + factor[5] + "','" + factor[6] + "');"); // �ǻ� Ʃ�� �߰�				
				
			}
			
			else if(selected_name.equals("Nurses")){

				stmt.executeUpdate("insert into nurses values(" + Integer.parseInt(factor[0]) + ",'" + factor[1] + "','" + factor[2]
						+ "','" + factor[3]+ "','" + factor[4] + "','" + factor[5] + "','" + factor[6] + "');"); // ��ȣ�� Ʃ�� �߰�			
				
			}
			
			else if(selected_name.equals("Patients")){

				stmt.executeUpdate("insert into patients values(" + Integer.parseInt(factor[0]) + "," + Integer.parseInt(factor[1]) + "," 
						+ Integer.parseInt(factor[2])+ ",'" + factor[3]+ "' ,'" + factor[4] + "'," + Integer.parseInt(factor[5]) + ",'" + factor[6]
						+"','" + factor[7] + "','" + factor[8] + "','"+ factor[9] +"');"); // ȯ�� Ʃ�� �߰�							
				
			}
			
			else if(selected_name.equals("Treatments")){
				stmt.executeUpdate("insert into treatments values("+ Integer.parseInt(factor[0]) +","+ Integer.parseInt(factor[1]) + ","
						+ Integer.parseInt(factor[2]) +",'"+ factor[3] + "', STR_TO_DATE('" + factor[4] + "','%Y-%m-%d'));"); // ���� Ʃ�� �߰�			
			}
			
			else if(selected_name.equals("Charts")){
				stmt.executeUpdate("insert into charts values('" + factor[0]+"'," + Integer.parseInt(factor[1]) + "," + Integer.parseInt(factor[2]) + "," 
						+ Integer.parseInt(factor[3]) + "," + Integer.parseInt(factor[4]) + ",'" + factor[5] + "');");
			}
			
			printArea.append(" ����");
			
		} catch (SQLException e) {
			System.out.println("SQL ���� ����");
			printArea.append(" ����, �ߺ��� Ű ���� ���ԵǾ� �ִ��� Ȯ�����ֽʽÿ�.");
		} catch (NumberFormatException e) {
			System.out.println("�Է� ��� ����");
			printArea.append(" ����, �Է°��� /�� ������ �Է����ֽʽÿ�.\n");
			
			if(selected_name.equals("Treatments")) {
				printArea.append("��¥�� yyyy-mm-dd �������� �Է����ֽʽÿ�.");
			}
			
		} catch (IndexOutOfBoundsException e) {
			System.out.println("�Է� ��� ����");
			printArea.append(" ����, �Է°��� /�� ������ �Է����ֽʽÿ�.\n");
		}
	   
	   
   }
      
   private void sqlRun() { // �˻� - ������̺� �޼ҵ�
	  
	   printArea.setText("");
	   
  	  String query="SELECT * FROM doctors"; /* SQL �� */
  	  try { /* �����ͺ��̽��� ���� ����� �������� ���� */
  	  	 Statement stmt=con.createStatement();
  	  	 ResultSet rs=stmt.executeQuery(query);
  	  	 printArea.append("doc_id\tmajor_treat\tdoc_name\tdoc_gen\tdoc_phone\tdoc_email\t\tdoc_position\n");
  	  	 while(rs.next()) {
  	  		printArea.append(""+rs.getInt(1));
  	  		printArea.append("\t"+rs.getString(2));
  	  		printArea.append("\t"+rs.getString(3));
 		  	printArea.append("\t"+rs.getString(4));
 		  	printArea.append("\t"+rs.getString(5));
  	  		printArea.append("\t"+rs.getString(6));
  	  		printArea.append("\t\t"+rs.getString(7)+"\n");
  	  	 }
  	  	 
  	  	 printArea.append("\n");  	  	
  	  	 
  	  	 query = "select * from nurses";
  	  	 rs = stmt.executeQuery(query);
  	  	 printArea.append("nur_id\tmajor_job\tnur_name\tnur_gen\tnur_phone\tnur_email\t\tnur_position\n");
 	  	 while(rs.next()) {
 	  		printArea.append(""+rs.getInt(1));
 	  		printArea.append("\t"+rs.getString(2));
 	  		printArea.append("\t"+rs.getString(3));
		  	printArea.append("\t"+rs.getString(4));
		  	printArea.append("\t"+rs.getString(5));
 	  		printArea.append("\t"+rs.getString(6));
 	  		printArea.append("\t\t"+rs.getString(7)+"\n");
 	  	 }
 	  	 
 	  	 printArea.append("\n");  
  	  	 
 	  	query = "select * from patients";
 	  	 rs = stmt.executeQuery(query);
 	  	 printArea.append("pat_id\tnur_id\tdoc_id\tpat_name\tpat_gen\tpat_jumin\tpat_addr\tpat_phone\tpat_email\tpat_job\n");
	  	 while(rs.next()) {
	  		printArea.append(""+rs.getInt(1));
	  		printArea.append("\t"+rs.getInt(2));
	  		printArea.append("\t"+rs.getInt(3));
		  	printArea.append("\t"+rs.getString(4));
		  	printArea.append("\t"+rs.getString(5));
	  		printArea.append("\t"+rs.getInt(6));
	  		printArea.append("\t"+rs.getString(7));
	  		printArea.append("\t"+rs.getString(8));
	  		printArea.append("\t"+rs.getString(9));
	  		printArea.append("\t"+rs.getString(10)+"\n");
	  	 }
	  	 
	  	 printArea.append("\n");  
 	  	 
	  	 query = "select * from treatments";
	  	 rs = stmt.executeQuery(query);
	  	 printArea.append("treat_id\tpat_id\tdoc_id\ttreat_contents\t\ttreat_date\n");
	  	 while(rs.next()) {
	  		printArea.append(""+rs.getInt(1));
	  		printArea.append("\t"+rs.getInt(2));
	  		printArea.append("\t"+rs.getInt(3));
	  		printArea.append("\t"+rs.getString(4));
	  		printArea.append("\t\t"+rs.getString(5)+"\n");
	  	 }
	  	 
	  	 printArea.append("\n");  
	  	 
	  	 query = "select * from charts";
	  	 rs = stmt.executeQuery(query);
	  	 printArea.append("chart_id\ttreat_id\tdoc_id\tpat_id\tnur_id\tchart_contents\n");
	  	 while(rs.next()) {
	  		printArea.append(""+rs.getString(1));
	  		printArea.append("\t"+rs.getInt(2));
	  		printArea.append("\t"+rs.getInt(3));
	  		printArea.append("\t"+rs.getInt(4));
	  		printArea.append("\t"+rs.getInt(5));
	  		printArea.append("\t"+rs.getString(6)+"\n");
	  	 }
	  	 
	  	 printArea.append("\n");  
	  	 
  	  	 //con.close();
  	  } catch(SQLException e) {
  	  	   e.printStackTrace();
  	    }
   }     
   
   private void sqlSearch1() { // �˻�1 - �躴�� �ǻ翡�� ������� ��� ȯ�ڿ� ���ؼ� ȯ���� �̸�, �ּ�, ��ȭ��ȣ �� ���� ������ ���� ��¥�� �˻��϶�
	   
	   printArea.setText("");
	   
	   String query="select pat_name, pat_addr, pat_phone, t1.treat_contents, t1.treat_date \r\n"
	   		+ "from (select pat_id, treat_contents, treat_date from treatments where doc_id = 91001) t1, patients \r\n"
	   		+ "where patients.pat_id = t1.pat_id order by pat_name;"; /* SQL �� */
	  	  try { /* �����ͺ��̽��� ���� ����� �������� ���� */
	  	  	 Statement stmt=con.createStatement();
	  	  	 ResultSet rs=stmt.executeQuery(query);
	  	  	 printArea.append("pat_name\tpat_addr\tpat_phone\t\ttreat_contents\t\ttreat_date\n");
	  	  	 while(rs.next()) {
	  	  		printArea.append(""+rs.getString(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	  	  		printArea.append("\t\t"+rs.getString(4));
	  	  		printArea.append("\t\t"+rs.getString(5)+"\n");
	  	  	 }
	  	  	 
	  	  	 printArea.append("\n");	  	  	
	  	  	 
	  	  	 //con.close();
	  	  } catch(SQLException e) {
	  	  	   e.printStackTrace();
	  	    }
	   
   }
   
   private void sqlSearch2() { // �˻�2 - ȯ�ڸ� ������ ����� ���� �ǻ翡 ���� ��� ������ �˻��϶�
	   
	   printArea.setText("");
	   
	   String query="select * from doctors where doc_id not in (select doc_id from treatments group by doc_id);"; /* SQL �� */
	  	  try { /* �����ͺ��̽��� ���� ����� �������� ���� */
	  	  	 Statement stmt=con.createStatement();
	  	  	 ResultSet rs=stmt.executeQuery(query);
	  	  	 
	  	  	 printArea.append("doc_id\tmajor_treat\tdoc_name\tdoc_gen\tdoc_phone\tdoc_email\t\tdoc_position\n");
	  	  	 while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	 		  	printArea.append("\t"+rs.getString(4));
	 		  	printArea.append("\t"+rs.getString(5));
	  	  		printArea.append("\t"+rs.getString(6));
	  	  		printArea.append("\t\t"+rs.getString(7)+"\n");
	  	  	 }
	  	  	 
	  	  	 printArea.append("\n");	  	  	
	  	  	 
	  	  	 //con.close();
	  	  } catch(SQLException e) {
	  	  	   e.printStackTrace();
	  	    }
	   
   }

   private void sqlSearch3() { // �˻�3 - ���� ���� ȯ�ڸ� ������ �ǻ翡 ���� ��� ������ �˻��϶�
	   
	   printArea.setText("�ִ밪�� ���� Ƚ���� ���� �ǻ簡 �ټ��� ��� ��� ���\n");
	   
	   String query="select d1.* \r\n"
	   		+ "from doctors d1, \r\n"
	   		+ "(select doc_id from (select doc_id, count(*) as 'treat_cnt'\r\n"
	   		+ "from treatments group by doc_id) cnt, (select max(treat_cnt) as maxi from (select doc_id, count(*) as 'treat_cnt' from treatments group by doc_id) cnt) maxx\r\n"
	   		+ "where maxx.maxi = cnt.treat_cnt) cnt1\r\n"
	   		+ "where d1.doc_id = cnt1.doc_id;"; /* SQL �� */
	  	  try { /* �����ͺ��̽��� ���� ����� �������� ���� */
	  	  	 Statement stmt=con.createStatement();
	  	  	 ResultSet rs=stmt.executeQuery(query);
	  	  	 
	  	  	 printArea.append("doc_id\tmajor_treat\tdoc_name\tdoc_gen\tdoc_phone\tdoc_email\t\tdoc_position\n");
	  	  	 while(rs.next()) {
	  	  		printArea.append(""+rs.getInt(1));
	  	  		printArea.append("\t"+rs.getString(2));
	  	  		printArea.append("\t"+rs.getString(3));
	 		  	printArea.append("\t"+rs.getString(4));
	 		  	printArea.append("\t"+rs.getString(5));
	  	  		printArea.append("\t"+rs.getString(6));
	  	  		printArea.append("\t\t"+rs.getString(7)+"\n");
	  	  	 }
	  	  	 
	  	  	 printArea.append("\n");	  	  	
	  	  	 
	  	  	 //con.close();
	  	  } catch(SQLException e) {
	  	  	   e.printStackTrace();
	  	    }
	   
   }
   
   
  	public static void main(String args[]) {
  	   
  		JC17011492M run=new JC17011492M();  	   

 	}
}