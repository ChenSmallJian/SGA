package com.chj.sga;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Queue;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

public class SGA {

	private double A, B;// 求解范围
	private int N;// 种群数量
	private double Pc, Pm;// 交叉概率,变异概率
	private int T;// 遗传代数
	private int a;// 求解精度

	private int K;// 染色体位数
	private String[] initialPopulation;// 初始DNA集合
	private double[] adaption;// 适应度集合
	private double[] probability;// 概率集合

	List<DNA> DNAs = new ArrayList<DNA>();// 初始种群集合

	private String ResultDNA = "";// 最优结果的DNA
	private double Result = 0d;// 最优结果

	// 统计量
	private int total_overlap = 0;// 总的交叉次数
	private int total_variation = 0;// 总的变异次数
	private Queue<DNA> queue = new LinkedList<DNA>();

	@SuppressWarnings("resource")
	public void getInfo() throws IOException {
		Scanner sc = new Scanner(System.in);
		BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("输入取值范围[A,B](以空格间隔):");
		String range = strin.readLine();
		String[] ranges = range.split(" ");
		A = Double.parseDouble(ranges[0]);
		B = Double.parseDouble(ranges[1]);

		System.out.print("输入初始种群数量N:");
		N = sc.nextInt();

		System.out.print("输入种群交叉概率Pc,种群变异概率Pm(以空格间隔):");
		String p = strin.readLine();
		String[] ps = p.split(" ");
		Pc = Double.parseDouble(ps[0]);
		Pm = Double.parseDouble(ps[1]);
		System.out.print("输入遗传代数T:");
		T = sc.nextInt();
		System.out.print("输入求解精度a(整数):");
		a = sc.nextInt();
		System.out.println("A:" + A + " B:" + B + " N:" + N + " Pc:" + Pc + " Pm:" + Pm + " T:" + T + " a:" + a);
	}

	/*
	 * 根据求解精度计算出染色体的位数
	 */
	public void init() {
		double S = Math.pow(10, a) * (B - A);
		int i = 0;
		while (S > 1) {
			S = S / 2;
			i++;
		}
		K = i;
		System.out.println("染色体的位数为:" + K);
	}

	/*
	 * 根据染色体的位数和初始种群的数量随机生成初始种群
	 */
	public void RandomDNA() {
		String DNA = "";
		double totalAdaption = 0;
		initialPopulation = new String[N];
		adaption = new double[N];
		probability = new double[N];
		Random rand = new Random();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < K; j++) {
				DNA += rand.nextInt(2) + "";
			}
			initialPopulation[i] = DNA;
			// 计算适应度
			adaption[i] = AdaptationFx(TwoIntoTen(DNA));
			// 适应度求和
			if (adaption[i] < 0)
				totalAdaption += -adaption[i];
			else
				totalAdaption += adaption[i];
			DNA = "";
		}
		// 计算每个个体的概率,保留6位精度
		// NumberFormat nf = NumberFormat.getInstance();
		// nf.setMinimumFractionDigits(6);
		for (int i = 0; i < N; i++) {
			// probability[i] = Double.parseDouble(nf.format(adaption[i] /
			// totalAdaption));
			if (adaption[i] < 0)
				probability[i] = -adaption[i] / totalAdaption;
			else
				probability[i] = adaption[i] / totalAdaption;
			DNAs.add(new DNA(i + 1, initialPopulation[i], adaption[i], probability[i]));
		}
		System.out.println("初始种群为:");
		for (DNA obj : DNAs)
			System.out.println(obj.toString());
	}

	/*
	 * 定义适应度函数
	 */
	public double AdaptationFx(double x2) {
		return x2 * Math.sin(10 * Math.PI * x2) + 1.0;
		/*
		 * double x3 = x2 * x2; x3 = 0 - x3; return x3 + 10;
		 */
		// return x2;
	}

	/*
	 * 将二进制字符串转换成对应区间上的十进制
	 */
	public double TwoIntoTen(String DNA) {
		if (DNA == "" | DNA == null)
			return 0d;
		int x1 = Integer.valueOf(DNA, 2);
		double x2 = A + x1 * (B - A) / (Math.pow(2, K) - 1);
		return x2;
	}

	/*
	 * 计算当前种群中适应度最大的染色体
	 */
	public void SelectBiggest() {
		for (String obj : initialPopulation) {
			double adaption = AdaptationFx(TwoIntoTen(obj));
			if (adaption >= Result | "".equals(ResultDNA)) {
				ResultDNA = obj;
				Result = adaption;
				// 最优解统计
				DNA dna = new DNA(0, obj, TwoIntoTen(obj), adaption);
				queue.add(dna);
			}
		}
	}

	// 进化过程
	public void Evolution() {
		int t = 0;
		int tt = 1;
		while (t < T) {
			System.out.println("*****************************************************第" + tt
					+ "次遗传变异*****************************************************");
			SelectBiggest();
			// 选择-复制
			ChooseAndCopy();
			int p = OverlapAmpunt();
			// 交叉
			Overlap(p);
			// 变异
			Variation();
			t++;
			tt++;
		}
		// 遗传算法结束
		System.out.println("算法完成:");
		System.out.println("总交叉次数为:" + total_overlap + ",总变异次数为:" + total_variation);
		System.out.println("寻求最优解的过程为:");
		int i = 1;
		for (DNA dna : queue) {
			System.out.println("第" + i + "次的最优染色体为:" + dna.getDNA() + ",对应数值为:" + dna.getAdaption() + ",最优解为:"
					+ dna.getProbability());
			i++;
		}
		// 绘制图表
		JFreeChart jfreechart = createChart(createDataset(queue));
		LineChart fjc = new LineChart("SGA", new ChartPanel(jfreechart));
		fjc.pack();
		RefineryUtilities.centerFrameOnScreen(fjc);
		fjc.setVisible(true);

		System.out.println("最优解的染色体为:" + ResultDNA + ",对应数值为:" + TwoIntoTen(ResultDNA));
		System.out.println("最优解为：" + Result);
	}

	// 生成图表主对象JFreeChart
	public static JFreeChart createChart(DefaultCategoryDataset linedataset) {
		// 定义图表对象
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);
		JFreeChart chart = ChartFactory.createLineChart("SGA-遗传算法", // 折线图名称
				"寻优次数", // 横坐标名称
				"最优值", // 纵坐标名称
				linedataset, // 数据
				PlotOrientation.VERTICAL, // 水平显示图像
				true, // include legend
				true, // tooltips
				false // urls
		);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setRangeGridlinesVisible(false); // 是否显示格子线
		return chart;
	}

	// 生成数据
	public static DefaultCategoryDataset createDataset(Queue<DNA> queue) {
		DefaultCategoryDataset linedataset = new DefaultCategoryDataset();
		// 各曲线名称
		String series1 = "sga";
		// 横轴名称(列名称)
		int i = 1;
		for (DNA dna : queue) {
			linedataset.addValue(dna.getProbability(), series1, i + "");
			i++;
		}
		return linedataset;
	}

	// 选择-复制
	public void ChooseAndCopy() {
		List<Double> orignalRates = new ArrayList<Double>(N);
		for (DNA dna : DNAs) {
			double probability = dna.getProbability();
			orignalRates.add(probability);
		}
		Map<Integer, Integer> count = new HashMap<Integer, Integer>();
		double num = N;
		for (int i = 0; i < num; i++) {
			int orignalIndex = LotteryUtil.lottery(orignalRates);

			Integer value = count.get(orignalIndex);
			count.put(orignalIndex, value == null ? 1 : value + 1);
		}
		System.out.println("选择-复制:");
		int i = 0;
		for (Entry<Integer, Integer> entry : count.entrySet()) {
			System.out.println(DNAs.get(entry.getKey()) + ", count=" + entry.getValue() + ", probability="
					+ entry.getValue() / num);
			int cou = entry.getValue();
			for (; cou > 0; cou--) {
				initialPopulation[i] = DNAs.get(entry.getKey()).getDNA();
				i++;
			}
		}
		System.out.println("选择-复制后的种群为:");
		for (String obj : initialPopulation) {
			System.out.println(obj);
		}
	}

	// 决定交叉的染色体数
	public int OverlapAmpunt() {
		int p = 0;
		for (int i = 0; i < N; i++) {
			double c = Math.random();
			if (c <= Pc) {
				p++;
			}
		}
		System.out.println("本次交叉操作的次数:" + p);
		total_overlap += p;
		return p;
	}

	// 交叉
	public void Overlap(int p) {
		while (p > 0) {
			Random ra = new Random();
			int m = ra.nextInt(N);
			int n;
			do {
				n = ra.nextInt(N);
			} while (n == m);
			System.out.print("选择的染色体为:" + m + "和" + n);
			// 选择交叉位(1到21位,默认不会进行22位交换)
			int k = ra.nextInt(K - 1) + 1;
			System.out.println(",交叉位数为:" + k);
			String m_front = initialPopulation[m].substring(0, K - k);
			String m_last = initialPopulation[m].substring(K - k);
			String n_front = initialPopulation[n].substring(0, K - k);
			String n_last = initialPopulation[m].substring(K - k);

			System.out.println("将" + initialPopulation[m] + "拆分为:" + m_front + "和" + m_last);
			System.out.println("将" + initialPopulation[n] + "拆分为:" + n_front + "和" + n_last);
			initialPopulation[m] = m_front + m_last;
			initialPopulation[n] = n_front + m_last;
			System.out.println("交叉后为:" + initialPopulation[m] + "和" + initialPopulation[n]);

			p--;
		}
		System.out.println("交叉完成后的种群为:");
		for (String obj : initialPopulation) {
			System.out.println(obj);
		}
	}

	// 变异
	public void Variation() {
		System.out.println("变异:");
		int k = N * K;
		System.out.println("总基因位数为:" + k);
		for (int i = 1; i <= k; i++) {
			double c = Math.random();
			System.out.print("i=" + i + ",c=" + c);
			if (c <= Pm) {
				System.out.println("<=Pm=" + Pm + "发生变异");
				total_variation++;
				// 表明该基因位要产生变异
				int m = i / K;
				int n = i % K;
				if (n == 0) {
					m = m - 1;
					n = K;
				}
				System.out.println("m=" + m + ",n=" + n);
				String gene = initialPopulation[m].substring(n - 1, n);
				int kk = m + 1;
				System.out.println("第" + kk + "条染色体:" + initialPopulation[m] + "的第" + n + "位发生变异");
				if ("0".equals(gene)) {
					initialPopulation[m] = initialPopulation[m].substring(0, n - 1) + "1"
							+ initialPopulation[m].substring(n);
				} else {
					initialPopulation[m] = initialPopulation[m].substring(0, n - 1) + "0"
							+ initialPopulation[m].substring(n);
				}
				System.out.println("变异完成后  :" + initialPopulation[m]);
			} else {
				System.out.println(">Pm=" + Pm + "不发生变异");
			}
		}
		System.out.println("变异完成后的种群为:");
		for (String obj : initialPopulation) {
			System.out.println(obj);
		}
	}
}