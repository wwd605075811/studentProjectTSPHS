package algorithm;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OnePointCrossover {
    public int [][]A;
    public int [][][]AList ;  //children and parents list
    public double  fitness[];    //fitness list
    public double MinCost;
    private int N;
    private int cityNum;
    private double p_c_t;
    private double p_m_t;
    private int MAX_GEN;
    private int bestLength;
    private int[] bestTour;
    private double bestFitness;
    private double[] averageFitness;
    private int[][] distance;
    private String filename;

    private List<Integer> PATH;

    public OnePointCrossover(int n, int num, int g, double p_c, double p_m) {
        this.N = n;
        this.cityNum = num;
        this.MAX_GEN = g;
        this.p_c_t = p_c;
        this.p_m_t = p_m;
        bestTour = new int[cityNum];
        averageFitness = new double[MAX_GEN];
        bestFitness = 0.0;
        distance = new int[cityNum][cityNum];
        this.PATH = new LinkedList<Integer>();
    }
    /**
     * initialization
     */
    public OnePointCrossover(){
        int r;
        int flag=0;
        A =new int[3][5];
        AList=new int[10][3][5];
        fitness=new double[10];
        MinCost=100000000;
//        for (int i = 0; i < 10; i++) {
//            List<Integer> l = new ArrayList<Integer>();
//            Random random = new Random();
//            while (l.size() < 15) {
//                r = random.nextInt(16);
//                if (!l.contains(r) && r != 0)
//                    l.add(r);
//            }
//           /* for (int j = 0; j <15 ; j++) {
//                System.out.print(l.get(j)+"  ");
//            }
//            System.out.println();*/
//            flag=0;
//            for (int k = 0; k <A.length ; k++) {
//                for (int j = 0; j < A[0].length; j++) {
//                    A[k][j]=l.get(flag);
//                    flag++;
//                    System.out.print(A[k][j]+"  ");
//                }
//                System.out.println();
//            }
//            AList[i]=A;
    }

    /**
     * reset the time
     */
    public void RenewAList(){
        //  Reset the cluster generator
        try {
            TimeUnit.SECONDS.sleep( 5 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pick out the best two individuals for crossover and mutation.
     * @param diedaicishu number of iterations
     */
    public void Evolution(int diedaicishu){//挑出最优的两个个体交叉+变异
        //挑出两个适应度最小个体
        double Min=fitness[0];
        int flag=0;
        int [][][]BList=new int[10][3][5] ;
        //先找到适应度最大，也就是fitness最小的
        for (int i = 0; i < fitness.length; i++) {
            if(fitness[i]<Min){
                flag=i;
                Min=fitness[i];
            }
            //System.out.println("最小的是:"+Min);
        }

        MinCost=Min;
        //System.out.println("最小的编号是:"+flag);
        int MinNum=flag;
        BList[0]=AList[flag];
        Min=fitness[0]+fitness[1];
        for (int i = 0; i < fitness.length; i++) {
            if(i==MinNum)
                continue;
            if(fitness[i]<Min){
                flag=i;
                Min=fitness[i];
            }
        }
//        System.out.println("第二小的是:"+Min);
//        System.out.println("第二小的编号是:"+flag);
        BList[1]=AList[flag];
        AList=BList;
        if(diedaicishu==1) {
            System.out.println("Keep the best two individuals as parents");
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < AList[0].length; j++) {
                    for (int k = 0; k < AList[0][0].length; k++) {
                        System.out.print(AList[i][j][k] + "   ");
                    }
                }
                System.out.println();
            }
        }

        //生成随机交叉点
        int Before=0,After=0,r=0;
        int [][][]CrossList=new int[2][3][5] ;
        int [][][]VarList=new int[2][5][3];
        int qqq=2;
        for (int i = 0; i <3; i++) {
            while (Before==0||After==0) {
                Random random = new Random();
                Before = random.nextInt(15);

                After = random.nextInt(15);
            }
            if(Before==After&&Before!=1)
                Before=Before-1;
            else if (Before==After&&Before==1)
                After=After+1;
            else if (Before>After){
                r=Before;
                Before=After;
                After=r;
            }
            CrossList= Cross(Before,After,diedaicishu);
            AList[qqq]=CrossList[0];
            qqq++;
            AList[qqq]=CrossList[1];
            qqq++;
        }
        if(diedaicishu==1){
            System.out.println("After cross                                                       cross point");
            for (int i = 2; i <4; i++) {
                for (int j = 0; j <AList[0].length; j++) {
                    for (int k = 0; k < AList[0][0].length; k++) {
                        System.out.print(AList[i][j][k]+"   ");
                    }
                }
                System.out.print(Before+"  "+After);
                System.out.println();
            }
        }
        VarList=Variation(diedaicishu);
        AList[8]=VarList[0];
        AList[9]=VarList[1];
    }

    /**
     * Find the best routing
     */
    public void FindBest() {
        //挑出两个适应度最小个体
        double Min = fitness[0];
        int flag=0;
        int [][][]BList=new int[10][3][5] ;
        //先找到适应度最大，也就是fitness最小的
        for (int i = 0; i < fitness.length; i++) {
            if(fitness[i]<Min){
                flag=i;
                Min=fitness[i];
            }
            //System.out.println("最小的是:"+Min);
        }

        MinCost=Min;
        //System.out.println("最小的编号是:"+flag);
        int MinNum=flag;
        BList[0]=AList[flag];
        Min=fitness[0]+fitness[1];
        for (int i = 0; i < fitness.length; i++) {
            if(i==MinNum)
                continue;
            if(fitness[i]<Min){
                flag=i;
                Min=fitness[i];
            }
        }
//        System.out.println("第二小的是:"+Min);
//        System.out.println("第二小的编号是:"+flag);
        BList[1]=AList[flag];
        AList=BList;

        System.out.println("The best routing is                                               cost:");
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < AList[0].length; j++) {
                for (int k = 0; k < AList[0][0].length; k++) {
                    System.out.print(AList[i][j][k] + "   ");
                }
            }
            System.out.println(Min);
        }
    }

    /**
     * Crossover a DNA.
     * @param Before front intersection
     * @param After rear intersection
     * @param diedaicishu number of iterations
     * @return new DNA
     */
    public int[][][] Cross(int Before, int After, int diedaicishu) {//根据随机生成的Before,After交叉点进行交叉
        int[][] Map;
        int qqq = 0;
        int[][][] CrossList = new int[2][3][5];
        int[][] CList = new int[2][15];

        for (int i = 0; i < 2; i++) {//把父代表现型提取出来准备进行交叉
            qqq = 0;
            for (int j = 0; j < AList[0].length; j++) {
                for (int k = 0; k < AList[0][0].length; k++) {
                    CList[i][qqq] = AList[i][j][k];
                    qqq++;
                }
            }
        }
       /*System.out.println("交叉前 ");
        for (int i = 0; i < CList.length; i++) {
            for (int j = 0; j < CList[0].length; j++) {
                System.out.print(CList[i][j]+" ");
            }
            System.out.println();
        }*/
        int flag = 0;   //标记映射关系中的重复元素
        int flagqqq = 0;
        int[][] FlagList = new int[2][15];
        FlagList = CList;
//        System.out.println("BEfore和After是："+Before+"  "+After);
        //清除重复映射关系
        for (int i = Before; i <= After; i++) {
            for (int j = Before; j <= After; j++) {
                if (FlagList[0][i] == FlagList[1][j]) {
                    FlagList[0][i] = 0;
                    FlagList[1][j] = 0;
                }
            }
        }
        for (int i = Before; i <= After; i++) {
            if (FlagList[0][i] == 0) {
                flag++;
            }
        }
        /*System.out.println(" Flaglist是:"+flag);
        for (int i = 0; i < FlagList.length; i++) {
            for (int j = 0; j < FlagList[0].length; j++) {
                System.out.print(FlagList[i][j]+" ");
            }
            System.out.println();
        }*/
        //保留映射关系
        Map = new int[2][After - Before + 1 - flag];
        flag = 0;
        for (int i = Before, j = 0; i <= After; i++) {
            if (FlagList[0][i] != 0) {
                Map[0][j] = FlagList[0][i];
                j++;
            }
        }
        for (int i = Before, j = 0; i <= After; i++) {
            if (FlagList[1][i] != 0) {
                Map[1][j] = FlagList[1][i];
                j++;
            }
        }
        /*System.out.println("MAp是: ");
        for (int i = 0; i < Map.length; i++) {
            for (int j = 0; j < Map[0].length; j++) {
                System.out.print(Map[i][j]+"  ");
            }
            System.out.println();
        }*/
        for (int i = 0; i < 2; i++) {//把父代表现型提取出来准备进行交叉
            qqq = 0;
            for (int j = 0; j < AList[0].length; j++) {
                for (int k = 0; k < AList[0][0].length; k++) {
                    CList[i][qqq] = AList[i][j][k];
                    qqq++;
                }
            }
        }
        /*System.out.println("原始数据");
        for (int i = 0; i < CList.length; i++) {
            for (int j = 0; j < CList[0].length; j++) {
                System.out.print(CList[i][j]+" ");
            }
            System.out.println();
        }*/
        //先对交叉区进行交换

        int[] rebegin = new int[After - Before + 1];

        for (int i = Before, j = 0; i <= After; i++) {
            rebegin[j] = CList[0][i];
            j++;
            CList[0][i] = CList[1][i];
        }
        for (int i = Before, j = 0; i <= After; i++) {
            CList[1][i] = rebegin[j];
            j++;
        }
       /* System.out.println("交叉映射区 ");
        for (int i = 0; i < CList.length; i++) {
            for (int j = 0; j < CList[0].length; j++) {
                System.out.print(CList[i][j]+" ");
            }
            System.out.println();
        }*/

        for (int i = 0; i < CList[0].length; i++) {
            if ((i >= Before) && (i <= After)) {
            } else
                for (int j = 0; j < Map[0].length; j++) {
                    if (CList[0][i] == Map[1][j])
                        CList[0][i] = Map[0][j];
                }
        }
        for (int i = 0; i < CList[1].length; i++) {
            if ((i >= Before) && (i <= After)) {
            } else
                for (int j = 0; j < Map[0].length; j++) {
                    if (CList[1][i] == Map[0][j])
                        CList[1][i] = Map[1][j];
                }
        }
        /*if (diedaicishu == 1) {
            System.out.println("交叉后                                                         交叉点"+Before+" "+After);
            for (int i = 0; i < CList.length; i++) {
                for (int j = 0; j < CList[0].length; j++) {
                    System.out.print(CList[i][j] + "  ");
                }
                System.out.println();
            }
        }*/
        qqq = 0;
        //把交叉完成后的返回
        for (int i = 0; i < CrossList.length; i++) {
            qqq = 0;
            for (int j = 0; j < CrossList[0].length; j++) {
                for (int k = 0; k < CrossList[0][0].length; k++, qqq++) {
                    CrossList[i][j][k] = CList[i][qqq];
                    //System.out.print(CrossList[i][j][k]+" ");
                }
            }
            //System.out.println();
        }
        return CrossList;
    }

    /**
     * mutate DNA.
     * @param diedaicishu the numbers of iterations
     * @return new DNA
     */
    public int[][][] Variation(int diedaicishu) {
        int[][] Map;
        int qqq = 0;
        int[][][] VarList = new int[2][3][5];
        int Before = 0, After = 0, r = 0;
        for (int i = 0; i < 2; i++) {
            VarList[i] = AList[i];
        }

        for (int i = 0; i < 2; i++) {
            while (Before == 0 || After == 0 || Before == 5 || Before == 10 || After == 5 || After == 10) {
                Random random = new Random();
                Before = random.nextInt(15);
                After = random.nextInt(15);
            }
            if (Before == 0)
                Before++;
            if (After == 0)
                After++;
            if (Before == After && Before != 1)
                Before = Before - 1;
            else if (Before == After && Before == 1)
                After = After + 1;
            else if (Before > After) {
                r = Before;
                Before = After;
                After = r;
            }
            if (Before % 5 == 0) {
                r = VarList[i][(Before / 5) - 1][4];
                VarList[i][(Before / 5) - 1][4] = VarList[i][After / 5][(After % 5) - 1];
                VarList[i][After / 5][(After % 5) - 1] = r;
            } else if (After % 5 == 0) {
                r = VarList[i][Before / 5][(Before % 5) - 1];
                VarList[i][Before / 5][(Before % 5) - 1] = VarList[i][(After / 5) - 1][4];
                VarList[i][(After / 5) - 1][4] = r;
            } else if ((Before % 5 == 0) && (After % 5 == 0)) {
                r = VarList[i][(Before / 5) - 1][4];
                VarList[i][(Before / 5)][4] = VarList[i][(After / 5) - 1][4];
                VarList[i][(After / 5) - 1][4] = r;
            } else {
                r = VarList[i][Before / 5][(Before % 5) - 1];
                VarList[i][Before / 5][(Before % 5) - 1] = VarList[i][After / 5][(After % 5) - 1];
                VarList[i][After / 5][(After % 5) - 1] = r;
            }
        }
        if (diedaicishu == 1) {
            System.out.println("After mutation                                                    mutation point");
            for (int i = 0; i < VarList.length; i++) {
                for (int j = 0; j < VarList[0].length; j++) {
                    for (int k = 0; k < VarList[0][0].length; k++) {
                        System.out.print(VarList[i][j][k] + "   ");
                    }
                }
                System.out.print(Before + "  " + After);
                System.out.println();
            }
        }
        return VarList;
    }
    public void solve() {

    }
    /**
     * Contar coding
     * @param s initial sort
     * @param n coding strategy
     * @return new sort
     */
    public int Contar(int s[], int n)
    {
        long []fac={1,1,2,6,24,120,720,5040,40320,362880,3628800,
                39916800,479001600};
        for (int i = 0; i < fac.length; i++) {
        }
        int i, j, cnt, sum;
        sum = 0;
        for (i = 0; i < n; ++i)
        {
            cnt = 0;
            for (j = i + 1; j < n; ++j)
                if (s[j] < s[i]) ++cnt;
            sum += cnt * fac[n - i - 1];
        }
        return sum;
    }

    public void RandomList() {
        List<Integer> l = new ArrayList<Integer>();
        //当链表种存在15个数时结束像链表种插入数据
        while (l.size() < 15) {
            int i = (int) (Math.random() * 15 + 1);
            if (!l.contains(i))
                l.add(i);
        }
        int flag=0;
        for (int i = 0; i <A.length ; i++) {
            for (int j = 0; j < A[0].length; j++) {
                A[i][j]=l.get(flag);
                flag++;
            }
        }
        for (int i = 0; i <A.length ; i++) {
            for (int j = 0; j < A[0].length; j++) {
                System.out.print(A[i][j]+"  ");
            }
        }

    }
    public List<Integer> getPATH() {
        return PATH;
    }

    public void setPATH(List<Integer> PATH) {
        this.PATH = PATH;
    }
}