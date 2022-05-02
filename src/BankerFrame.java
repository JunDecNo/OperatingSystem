import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BankerFrame implements ActionListener{
    public JFrame jFrame;
    public JPanel jPanel;
    public JScrollPane jScrollPane;
    public JTable jTable;
    public JButton BtnCre,BtnReq;
    public String[]processName,ResourceName;
    public int[]Available,Work;
    public int[][]Max,Allocation,Need;
    public boolean[]Finish;
    public DefaultTableModel model;
    BankerFrame(){
        String str[]={"进程名","Work","Max","Allocation","Need","Work+Allocation"};
        model=new DefaultTableModel(str,0);
        jTable=new JTable(model);
        jFrame=new JFrame("银行家算法");
        jPanel=new JPanel();
        jScrollPane=new JScrollPane(jTable);
        BtnCre=new JButton("创建");
        BtnReq=new JButton("请求");
        Setting();
        Add();
    }
    void Add(){
        BtnCre.addActionListener(this);
        BtnReq.addActionListener(this);
        jPanel.add(BtnCre);jPanel.add(BtnReq);
        jFrame.add(jPanel,BorderLayout.SOUTH);
        jFrame.add(jScrollPane,BorderLayout.CENTER);
    }
    void Setting(){
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        jTable.setRowHeight(30);
        jTable.setDefaultRenderer(Object.class,r);
        jFrame.setLayout(new BorderLayout());
        jFrame.setBounds(100,100,1000,600);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==BtnCre)
            new Create(jFrame.getWidth()/4+jFrame.getX(),jFrame.getHeight()/4+jFrame.getY(),this);
        if (actionEvent.getSource()==BtnReq){
            if (Available==null){
                JOptionPane.showMessageDialog(jFrame,"请先创建数据","提示",JOptionPane.ERROR_MESSAGE);
                return;
            }
            String str=JOptionPane.showInputDialog(jFrame,"请输入请求信息","输入",JOptionPane.INFORMATION_MESSAGE);
            if (!str.isEmpty()){
                String []array=str.split("\40");
                if (array.length-1!=Available.length)
                    JOptionPane.showMessageDialog(jFrame,"输入非法","提示",JOptionPane.ERROR_MESSAGE);
                else {
                    int len=array.length;
                    int []Arr=new int[len-1];
                    for (int i=0;i<len-1;i++){
                        Arr[i]=Integer.parseInt(array[i+1]);
                    }
                    Safety(array[0],Arr);
                }
            }
        }
    }
    void Safety(String name,int []request){
        int i=0,j=0,cnt=0,id,len=processName.length,ava=Available.length;
        boolean isSafe=true;
        Finish=new boolean[len];
        while(i<len&&!processName[i].equals(name))i++;
        if (i==len){
            JOptionPane.showMessageDialog(jFrame,"未找到进程","提示",JOptionPane.ERROR_MESSAGE);
            return;
        }
        id=i;
        if (request.length==ava){
            for (i=0;i<ava;i++){
                if(request[i]>Need[id][i]){
                    JOptionPane.showMessageDialog(jFrame,"超过宣布的最大值","提示",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(request[i]>Available[i]){
                    JOptionPane.showMessageDialog(jFrame,"尚无足够资源","提示",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            for (i=0;i<ava;i++){
                Available[i]-=request[i];
                Allocation[id][i]+=request[i];
                Need[id][i]-=request[i];
            }
            Work=new int[ava];
            Work=Available.clone();
            int []WorkT=Available.clone();
            //执行安全性算法
            while (cnt<len){
                for (i=0;i<len;i++) {
                    if (Finish[i])continue;
                    boolean flag = true;
                    for (j = 0; j < ava; j++) {
                        if (Need[i][j] > Work[j]) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        cnt++;
                        for (j=0;j<ava;j++) {
                            Work[j] += Allocation[i][j];
                        }
                        Finish[i]=true;break;
                    }
                }
                if(i==len)break;
            }
            for (i=0;i<len;i++){
                if(!Finish[i]){
                    isSafe=false;break;
                }
            }
            if (!isSafe){
                JOptionPane.showMessageDialog(jFrame,"不存在安全序列","提示",JOptionPane.ERROR_MESSAGE);
                for (i=0;i<ava;i++){
                    Available[i]+=request[i];
                    Allocation[id][i]-=request[i];
                    Need[id][i]+=request[i];
                }
            }else{
                model.setRowCount(0);
                Work=WorkT;
                StringBuilder stringBuilder=new StringBuilder();
                for (i=0;i<ava;i++){
                    stringBuilder.append(ResourceName[i]+" ");
                }
                String str=String.valueOf(stringBuilder);
                String []string={" ",str,str, str, str,str};
                model.addRow(string);
                for (i=0;i<len;i++)Finish[i]=false;
                cnt=0;
                while (cnt<=len){
                    for (i=0;i<len;i++) {
                        if(Finish[i])continue;
                        boolean flag = true;
                        for (j = 0; j < ava; j++) {
                            if (Need[i][j] > Work[j]) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            cnt++;
                            int []before=Work.clone();
                            for (j=0;j<ava;j++) {
                                Work[j] += Allocation[i][j];
                            }
                            StringBuilder max= new StringBuilder();
                            StringBuilder work=new StringBuilder();
                            StringBuilder allow=new StringBuilder();
                            StringBuilder need=new StringBuilder();
                            StringBuilder available=new StringBuilder();
                            for(j=0;j<ava;j++){
                                work.append(before[j]+" ");
                                max.append(Max[i][j]+" ");
                                allow.append(Allocation[i][j]+" ");
                                need.append(Need[i][j]+" ");
                                available.append(Work[j]+" ");
                            }
                            String []strings={processName[i],String.valueOf(work),String.valueOf(max), String.valueOf(allow), String.valueOf(need), String.valueOf(available)};
                            model.addRow(strings);
                            Finish[i]=true;break;
                        }
                    }
                    if(i==len)break;
                }
            }
        }
    }
}
class Create implements ActionListener {
    public JFrame jFrame;
    public JScrollPane S_Max,S_Allocation;
    public JTextField kind,process,count;
    public JTextArea Max,Allocation;
    public JLabel L_kind,L_process,L_count,L_Max,L_Allocation;
    public JButton Commit;
    public BankerFrame bankerFrame;
    Create(int x,int y,BankerFrame bankerFrame){
        this.bankerFrame=bankerFrame;
        jFrame=new JFrame("创建");
        kind=new JTextField(20);
        process=new JTextField(20);
        count=new JTextField(20);
        Max=new JTextArea(8,20);
        Allocation=new JTextArea(8,20);
        S_Max=new JScrollPane(Max);
        S_Allocation=new JScrollPane(Allocation);
        L_kind=new JLabel("资源种类");
        L_process=new JLabel("进程名   ");
        L_count=new JLabel("资源数目");
        L_Max=new JLabel("进程的最大需求");
        L_Allocation=new JLabel("进程的分配资源");
        Commit=new JButton("提交");
        Commit.addActionListener(this);
        jFrame.setLayout(new FlowLayout());
        jFrame.add(L_process);jFrame.add(process);
        jFrame.add(L_kind);jFrame.add(kind);
        jFrame.add(L_count);jFrame.add(count);
        jFrame.add(L_Max);jFrame.add(S_Max);
        jFrame.add(L_Allocation);jFrame.add(S_Allocation);
        jFrame.add(Commit);
        jFrame.setBounds(x,y,260,560);
        jFrame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource()==Commit){
            bankerFrame.model.setRowCount(0);
            String []strMax=Max.getText().split("\n"),
                    strCnt=count.getText().split("\40"),
                    strPro=process.getText().split("\40"),
                    strKind=kind.getText().split("\40"),
                    strAllocation=Allocation.getText().split("\n");
            int len_max=strMax.length,len_cnt=strCnt.length,len_pro=strPro.length,
                    len_kind=strKind.length,len_allocation=strAllocation.length;
            if(len_cnt!=len_kind){
                JOptionPane.showMessageDialog(jFrame,"输入非法","错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            bankerFrame.processName=strPro;
            bankerFrame.ResourceName=strKind;
            bankerFrame.Max=new int[len_pro][len_kind];
            bankerFrame.Allocation=new int[len_pro][len_kind];
            bankerFrame.Need=new int[len_pro][len_kind];
            bankerFrame.Available=new int[len_cnt];
            for (int i=0;i<len_cnt;i++){
                bankerFrame.Available[i]=Integer.parseInt(strCnt[i]);
            }
            for (int i=0;i<len_pro;i++){
                String []string;
                int len;
                if (i<len_max){
                    string=strMax[i].split("\40");
                    len=string.length;
                }
                else {
                    string=new String[len_cnt];
                    len=0;
                }
                for (int j=0;j<len_kind;j++){
                    if(j>=len||string[j].equals(""))bankerFrame.Max[i][j]=0;
                    else bankerFrame.Max[i][j]=Integer.parseInt(string[j]);
                }
            }
            for (int i=0;i<len_pro;i++){
                String []string;
                int len;
                if (i<len_allocation){
                    string=strAllocation[i].split("\40");
                    len=string.length;
                }
                else {
                    string=new String[len_cnt];
                    len=0;
                }
                for (int j=0;j<len_kind;j++){
                    if (j>=len||string[j].equals(""))bankerFrame.Allocation[i][j]=0;
                    else bankerFrame.Allocation[i][j]=Integer.parseInt(string[j]);
                }
            }
            for(int i=0;i<len_pro;i++){
                for (int j=0;j<len_cnt;j++){
                    bankerFrame.Need[i][j]=bankerFrame.Max[i][j]-bankerFrame.Allocation[i][j];
                    bankerFrame.Available[j]-=bankerFrame.Allocation[i][j];
                }
            }
//            StringBuilder stringBuilder=new StringBuilder();
//            for (int i=0;i<len_cnt;i++){
//                stringBuilder.append(strKind[i]+" ");
//            }
//            String str=String.valueOf(stringBuilder);
//            String []string={" ",str,str, str, str};
//            bankerFrame.model.addRow(string);
//            for (int i=0;i<len_pro;i++){
//                StringBuilder max= new StringBuilder();
//                StringBuilder allow=new StringBuilder();
//                StringBuilder need=new StringBuilder();
//                StringBuilder available=new StringBuilder();
//                for(int j=0;j<len_cnt;j++){
//                    max.append(bankerFrame.Max[i][j]+" ");
//                    allow.append(bankerFrame.Allocation[i][j]+" ");
//                    need.append(bankerFrame.Need[i][j]+" ");
//                    available.append(bankerFrame.Available[j]+" ");
//                }
//                String []strings={strPro[i], String.valueOf(max), String.valueOf(allow), String.valueOf(need), String.valueOf(available)};
//                bankerFrame.model.addRow(strings);
//            }
            int []res=new int[len_cnt];
            for (int i=0;i<len_cnt;i++)res[i]=0;
            bankerFrame.Safety(strPro[0],res);
            jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jFrame.dispose();
        }
    }
}