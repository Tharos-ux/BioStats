package Main;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.Sensors;
import oshi.hardware.VirtualMemory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.jezhumble.javasysmon.JavaSysMon;

public class Main {
	
	/**
	 * length of key used for alignment
	 */
	public static int keySize = 22;
	public static int execs = 1;
	public static ArrayList<String> tableArgs = new ArrayList<String>();
	public static Scanner sc = new Scanner(System.in);
	public static SystemInfo systemInfo = new SystemInfo();
	public static HardwareAbstractionLayer hardware = systemInfo.getHardware();
	public static ArrayList<String> pathList = new ArrayList<String>();
	public static String sysname = getComputerName();
	public static Sensors sensor = hardware.getSensors();
	public static final String intitules = "000100 pb,000500 pb,001000 pb,005000 pb,010000 pb,050000 pb,100000 pb,Temperature maximale,Delta temperature,Nom processeur,Cadence horloge,Architecture processeur,Fabricant processeur,Microarchitecture,Processeurs physiques,Processeurs logiques,Processeurs JVM,Mémoire JVM,Mémoire totale,Vitesse mémoire,Type mémoire,OS";
	
	private static String getComputerName(){
	    Map<String, String> env = System.getenv();
	    if (env.containsKey("COMPUTERNAME"))
	        return env.get("COMPUTERNAME");
	    else if (env.containsKey("HOSTNAME"))
	        return env.get("HOSTNAME");
	    else
	        return "Unknown Computer";
	}	
			
	/**
	 * main process, auto start
	 * @param args system params
	 */
	public static void main(String[] args) {
		// tasks to do
		pathList.add("ressources/Escherichia_coli_fraction0000100_READS_MIXED.fasta");
		pathList.add("ressources/Escherichia_coli_fraction0000500_READS_MIXED.fasta");
		pathList.add("ressources/Escherichia_coli_fraction0001000_READS_MIXED.fasta");
		pathList.add("ressources/Escherichia_coli_fraction0005000_READS_MIXED.fasta");
		pathList.add("ressources/Escherichia_coli_fraction0010000_READS_MIXED.fasta");
		pathList.add("ressources/Escherichia_coli_fraction0050000_READS_MIXED.fasta");
		pathList.add("ressources/Escherichia_coli_fraction0100000_READS_MIXED.fasta");
		
		System.out.println("-------------- EXEC ---------------");
		
		System.out.println("Temps d'exécution moyen sur "+execs+" exécutions avec clés de "+keySize+" pb. :");
		System.out.println();
		double startTemp = sensor.getCpuTemperature();
		double tempp = executionCode(execs);
		tempp = Math.round(tempp*100.0)/100.0;
		System.out.println();
		System.out.println("Température maximale atteinte : "+tempp+"°C");
		tableArgs.add(Double.toString(tempp));
		tableArgs.add(Double.toString(tempp-startTemp));
		
		System.out.println("------------- SYSINFO -------------");
		getSysInfo();
		
		System.out.println("--------------- ASK ---------------");
		addInfoPrompt();
		
		// closing scanner
		sc.close();
	}
	
	/**
	 * main loop
	 * @param executions number of times calculations are done
	 * @return maximum CPU temperature in degrees
	 */
	public static double executionCode(int executions) {
		double averageTemp = 0;
		ArrayList<Double> temps = new ArrayList<Double>();
		long startTime;
		averageTemp = sensor.getCpuTemperature();
		for(String s : pathList) {
			// Save command starting time
			startTime = System.nanoTime();
			for(int i=0;i<executions;i++) {
				Backend.exec(Charge.load(s),keySize);
				temps.add(sensor.getCpuTemperature());
			}
			// Print out processing time
			System.out.println(s.substring(11,s.length()-6) +" --> " + ((System.nanoTime()-startTime)/executions) / 1000000+" ms");
			tableArgs.add(Long.toString(((System.nanoTime()-startTime)/executions) / 1000000));
		}
		return averageTemp>=Collections.max(temps)?averageTemp:Collections.max(temps);
	}
	
	/**
	 * auto-collect info from system
	 */
	public static void getSysInfo() {
		CentralProcessor processor = hardware.getProcessor();
		GlobalMemory memory = hardware.getMemory();
		List<PhysicalMemory> phyM = memory.getPhysicalMemory();
		VirtualMemory virM = memory.getVirtualMemory();
		long gMem = 0;
		long gSpeed = phyM.isEmpty()?0:(phyM.get(0).getClockSpeed())/1000000;
		for(PhysicalMemory m : phyM) {
			gMem = gMem+(m.getCapacity()/1048576);
			gSpeed = gSpeed<=m.getClockSpeed()/1000000?gSpeed:m.getClockSpeed()/1000000;
		}
		if(gMem==0) gMem = virM.getVirtualMax();
		CentralProcessor.ProcessorIdentifier processorIdentifier = processor.getProcessorIdentifier();
		JavaSysMon monitor = new JavaSysMon();
		String cpuSpeed = Long.toString((monitor.cpuFrequencyInHz())/1000000);
		System.out.println("Nom du processeur : "+ processorIdentifier.getName());
		tableArgs.add(processorIdentifier.getName());
		System.out.println("Cadence de l'horloge : "+ cpuSpeed + " GHz");
		tableArgs.add(cpuSpeed);
		System.out.println("Architecture du processeur : "+ System.getProperty("os.arch"));
		tableArgs.add(System.getProperty("os.arch"));
		System.out.println("Fabricant du processeur : "+ processorIdentifier.getVendor());
		tableArgs.add(processorIdentifier.getVendor());
		System.out.println("Microarchitecture processeur : "+ processorIdentifier.getMicroarchitecture());
		tableArgs.add(processorIdentifier.getMicroarchitecture());
		System.out.println("Processeurs physiques : " + processor.getPhysicalProcessorCount() + " coeurs physiques");
		tableArgs.add(Integer.toString(processor.getPhysicalProcessorCount()));
		System.out.println("Processeurs logiques : " + processor.getLogicalProcessorCount() + " coeurs logiques");
		tableArgs.add(Integer.toString(processor.getLogicalProcessorCount()));
		System.out.println("Processeurs alloués à la JVM : " + Runtime.getRuntime().availableProcessors() + " coeurs logiques");
		tableArgs.add(Integer.toString(Runtime.getRuntime().availableProcessors()));
		System.out.println("Taille mémoire allouée à la JVM : " + Runtime.getRuntime().totalMemory()/1048576 + " Mo");
		tableArgs.add(Long.toString(Runtime.getRuntime().totalMemory()/1048576));
		System.out.println("Taille mémoire totale : " + gMem + " Mo");
		tableArgs.add(Long.toString(gMem));
		System.out.println("Vitesse mémoire : "+ gSpeed + " MHz");
		tableArgs.add(Long.toString(gSpeed));
		System.out.println("Type mémoire : "+ ((phyM.isEmpty())?"VIRTUAL":phyM.get(0).getMemoryType()));
		tableArgs.add(((phyM.isEmpty())?"VIRTUAL":phyM.get(0).getMemoryType()));
		System.out.println("Système d'exploitation : " + System.getProperty("os.name"));
		tableArgs.add(System.getProperty("os.name"));
	}
	
	
	public static String csvCompiler(ArrayList<String> args) {
		String chain = args.get(0);
		for(String e : args) {
			chain+=','+e;
		}
		return chain;
	}
	
	public static void writd(String str) throws IOException {
			    BufferedWriter writer = new BufferedWriter(new FileWriter("test.csv"));
			    writer.write(intitules+"\n");
			    writer.write(str);
			    writer.close();
			}
	
	/**
	 * user-asking interface for adding to CSV file
	 */
	public static void addInfoPrompt() {
		boolean answer = false;
		// ajouter ou non la ligne au CSV (Y/N?)
		System.out.println("Tapez 'y' pour enregistrer ces données.");
		// module pour enregistrer
		String p = sc.nextLine();
		if(p.equals("y") || p.equals("Y")) {
			answer = true;
		}
		// save only if user did ask so
		if(answer) {
			//Output.rec(tableArgs);
			//Output.ecriture(sysname,tableArgs.toString());
			try {
				writd(csvCompiler(tableArgs));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Données enregistrées avec succès !");
		}
		else {
			System.out.println("Abandon et terminaison...");
		}
	}
}

