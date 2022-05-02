import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.Buffer;
import java.util.EventListener;

public class Main {
    public static void main(String args[]){
        JF jf=new JF();
    }
}

class JF implements ActionListener{
    JFrame jFrame;
    JMenuBar jMenuBar = new JMenuBar();
    JMenu file,edit,process,memory,device;
    JMenuItem f_open,f_save,f_exit;
    JMenuItem e_allow,e_ban;
    JMenuItem p_one,p_two,p_three,p_four;
    JMenuItem m_one,m_two;
    JMenuItem d_disk;
    JTextArea jTextArea;
    JScrollPane jScrollPane;
    JPanel jPanel;
    FileDialog open,save;
    JButton btn_open,btn_save,btn_exit;
    JLabel jLabel;
    ImageIcon icon_open,icon_save,icon_exit;
    JF(){
        //初始化所有的组件
        jFrame=new JFrame("操作系统辅助教学系统");
        jTextArea=new JTextArea(15,20);
        jScrollPane=new JScrollPane(jTextArea);
        jPanel=new JPanel();
        jLabel=new JLabel("实验内容---------------------------------------------");
        icon_open=new ImageIcon(Main.class.getResource("/images/open.png"));
        icon_save=new ImageIcon(Main.class.getResource("/images/save.png"));
        icon_exit=new ImageIcon(Main.class.getResource("/images/exit.png"));

        jFrame.setBounds(100,100,900,500);
        open=new FileDialog(jFrame,"打开文件",FileDialog.LOAD);
        save=new FileDialog(jFrame,"保存文件",FileDialog.SAVE);
        file=new JMenu("文件(F)");
        file.setMnemonic(KeyEvent.VK_F);
        edit=new JMenu("编辑(E)");
        edit.setMnemonic(KeyEvent.VK_E);
        process=new JMenu("进程管理(P)");
        process.setMnemonic(KeyEvent.VK_P);
        memory=new JMenu("存储管理(M)");
        memory.setMnemonic(KeyEvent.VK_M);
        device=new JMenu("设备管理(D)");
        f_open=new JMenuItem("打开(O)");
        f_save=new JMenuItem("保存(S)");
        f_exit=new JMenuItem("退出(W)");
        f_exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        f_open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_DOWN_MASK));
        f_save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK));
        e_allow=new JMenuItem("允许编辑");
        e_ban=new JMenuItem("禁止编辑");
        p_one=new JMenuItem("进程状态转换");
        p_two=new JMenuItem("生产者-消费者");
        p_three=new JMenuItem("进程调度");
        p_four=new JMenuItem("银行家算法");
        m_one=new JMenuItem("动态分区分配算法");
        m_two=new JMenuItem("页面置换算法");
        d_disk=new JMenuItem("磁盘调度算法");
        btn_open=new JButton("打开");
        btn_save=new JButton("保存");
        btn_exit=new JButton("退出");
        btn_open.setIcon(new ImageIcon(icon_open.getImage().getScaledInstance(20,20,1)));
        btn_save.setIcon(new ImageIcon(icon_save.getImage().getScaledInstance(20,20,1)));
        btn_exit.setIcon(new ImageIcon(icon_exit.getImage().getScaledInstance(20,20,1)));
        //文件绑定事件
        f_open.addActionListener(this);
        f_exit.addActionListener(this);
        f_save.addActionListener(this);
        //文本编辑事件
        e_allow.addActionListener(this);
        e_ban.addActionListener(this);
        //按钮绑定事件
        btn_open.addActionListener(this);
        btn_save.addActionListener(this);
        btn_exit.addActionListener(this);
        //算法绑定事件
        p_one.addActionListener(this);
        p_two.addActionListener(this);
        p_three.addActionListener(this);
        p_four.addActionListener(this);
        //存储管理
        m_one.addActionListener(this);
        m_two.addActionListener(this);
        d_disk.addActionListener(this);
        //窗口
        //添加其他监听器
        this.OtherListener();
        //添加组件
        this.AddComponent();
        //设置属性
        this.Setting();
        jFrame.setResizable(true);
        jFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent event){
        if (event.getSource()==f_exit||event.getSource()==btn_exit)jFrame.dispose();//关闭JFrame
        if (event.getSource()==f_open||event.getSource()==btn_open)file_open();//打开文件打开框
        if (event.getSource()==f_save||event.getSource()==btn_save)file_save();//打开文件保存框
        if (event.getSource()==e_allow)jTextArea.setEditable(true);//文本框可编辑
        if (event.getSource()==e_ban)jTextArea.setEditable(false);//文件不可编辑
        if(event.getSource()==p_one)new ProcessFrame();
        if(event.getSource()==p_two)new ProduceFrame();
        if (event.getSource()==p_three)new ProcessScheduling();
        if (event.getSource()==p_four)new BankerFrame();
        if(event.getSource()==m_two)new PageReplace();
        if(event.getSource()==m_one)new DynamicPartition();
        if (event.getSource()==d_disk)new DiskScheduling();
    }
    void file_open(){
        File file;
        open.setVisible(true);
        String DirPath=open.getDirectory();
        String FileName=open.getFile();
        if (DirPath!=null&&FileName!=null){
            file=new File(DirPath,FileName);//实例文件对象
            jTextArea.setText(null);//对于打开新文件，在对象建立的情况下，就清空文本域
            try {
                BufferedReader bufferedReader=new BufferedReader(new FileReader(file));//创建文件读取缓冲流
                String line=null;//初始化字符串，用于获取每行的字符串
                while((line=bufferedReader.readLine())!=null){
                    jTextArea.append(line+'\n');//在每次读取一行时，换行
                }
                bufferedReader.close();//关闭缓冲流
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("文件读取失败");
            }
        }

    }
    void file_save(){
        save.setVisible(true);
        String DirPath=save.getDirectory();
        String FileName=save.getFile();
        File file;
        if (DirPath!=null&&FileName!=null){
            file=new File(DirPath,FileName);//实例文件对象
            try {
                BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(file));//创建文件读取缓冲流
                String string=jTextArea.getText();//获取文本域的字符串
                bufferedWriter.write(string);
                bufferedWriter.close();//关闭缓冲流
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void OtherListener(){
        jFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);//为了在JFrame的大小变化时，流式布局不会改变
                jLabel.setPreferredSize(new Dimension(jFrame.getWidth(),20));
                jPanel.setPreferredSize(new Dimension(jFrame.getWidth(),60));
            }
        });
    }
    private void AddComponent(){//此方法用于添加所有的组件和容器
        jPanel.add(btn_open);jPanel.add(btn_save);
        jPanel.add(btn_exit);jPanel.add(jLabel);
        file.add(f_open);file.add(f_save);file.add(f_exit);
        edit.add(e_allow);edit.add(e_ban);
        process.add(p_one);process.add(p_two);
        process.add(p_three);process.add(p_four);
        memory.add(m_one);memory.add(m_two);
        device.add(d_disk);
        jMenuBar.add(file);jMenuBar.add(edit);jMenuBar.add(process);jMenuBar.add(memory);jMenuBar.add(device);
        jFrame.setJMenuBar(jMenuBar);
        jFrame.add(jPanel,BorderLayout.NORTH);
        jFrame.add(jScrollPane,BorderLayout.CENTER);
    }
    private void Setting(){//此方法用于设置组件的属性
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//关闭，直接关闭
        jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));//向左浮动布局
        jLabel.setPreferredSize(new Dimension(jFrame.getWidth(),20));
        jPanel.setPreferredSize(new Dimension(jFrame.getWidth(),60));
        btn_open.setContentAreaFilled(false);//去除背景
        btn_save.setContentAreaFilled(false);
        btn_exit.setContentAreaFilled(false);
        btn_open.setFocusPainted(false);//去除被选中的框
        btn_save.setFocusPainted(false);
        btn_exit.setFocusPainted(false);
        jLabel.setFont(new Font("楷体",Font.BOLD,16));//设置字体
        jTextArea.setLineWrap(true);
        jTextArea.setFont(new Font("宋体",Font.PLAIN,20));//设置字体
    }
}