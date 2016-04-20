package com.wireless.BPNN;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		Main main = new Main();
		main.partSampleTrain();
		main.allSampleTrain();
	}
	
	private void allSampleTrain(){
		/*
		 * Sample Size
		 */ 
		int sampleSize;
		/*
		 * Input Size
		 */
		int inputSize;
		/*
		 * Output Size
		 */
		int outputSize;
		Scanner in = new Scanner(System.in);
		/*System.out.print("Please input the sample size: ");
		sampleSize = in.nextInt();
		System.out.print("Please input the parameter size: ");
		inputSize = in.nextInt();
		System.out.print("Please input the output size: ");
		outputSize =in.nextInt();*/
		sampleSize = 81;
		inputSize = 5;
		outputSize = 1;
		double[][] sampleData = new double[sampleSize][inputSize];
		double[][] optData = new double[sampleSize][outputSize];
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("E:/subj_trainvalid-revised.xls");
			jxl.Workbook readwb = Workbook.getWorkbook(fin);
			Sheet readSheet = readwb.getSheet(0);
			int rsColums = readSheet.getColumns();
			int rsRows = readSheet.getRows();
			for(int i = 1; i < rsRows; ++i){
				for(int j = 0; j < 6; ++j){
					Cell cell = readSheet.getCell(j, i);
					if(5 == j){
						optData[i - 1][0] = Double.parseDouble(cell.getContents());
					}else{
						sampleData[i - 1][j] = Double.parseDouble(cell.getContents());
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(fin != null){
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		BPNN bp = new BPNN(sampleSize, inputSize, outputSize, 20, 0.1);
		bp.Train(sampleData, optData);
		
		//以下为存储实验结果到excel文档中
		FileOutputStream fos;
		WritableWorkbook wwb = null;
		NumberFormat nf = new NumberFormat("#.##");
		WritableCellFormat wcf = new WritableCellFormat(nf);
		try {
			fos = new FileOutputStream("E:/AllSamResult.xls");
			wwb = Workbook.createWorkbook(fos);
			WritableSheet ws = wwb.createSheet("Sheet1", 0);
			Label label = new Label(0, 0, "SampleSize:81; Neuron:20; LearningRate:0.1");
			ws.addCell(label);
			label = new Label(0, 1, "Desired Result");
			ws.addCell(label);
			label = new Label(1, 1, "Estimated Result");
			ws.addCell(label);
			jxl.write.Number number = null;
			for(int i = 0; i < sampleSize; ++i){
				//System.out.println("Desired result: " + optData[i][0] + "; " + "Result: " + bp.getResult(sampleData[i])[0]);
				number = new jxl.write.Number(0, i + 2, optData[i][0], wcf);
				ws.addCell(number);
				number = new jxl.write.Number(1, i + 2, bp.getResult(sampleData[i])[0], wcf);
				ws.addCell(number);
			}
			wwb.write();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(wwb != null){
				try {
					wwb.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void partSampleTrain(){
		/*
		 * Sample Size
		 */ 
		int sampleSize;
		/*
		 * Input Size
		 */
		int inputSize;
		/*
		 * Output Size
		 */
		int outputSize;
		Scanner in = new Scanner(System.in);
		/*System.out.print("Please input the sample size: ");
		sampleSize = in.nextInt();
		System.out.print("Please input the parameter size: ");
		inputSize = in.nextInt();
		System.out.print("Please input the output size: ");
		outputSize =in.nextInt();*/
		sampleSize = 70;
		inputSize = 5;
		outputSize = 1;
		double[][] sampleData = new double[sampleSize][inputSize];
		double[][] optData = new double[sampleSize][outputSize];
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("E:/subj_trainvalid-revised.xls");
			jxl.Workbook readwb = Workbook.getWorkbook(fin);
			Sheet readSheet = readwb.getSheet(0);
			int rsColums = readSheet.getColumns();
			int rsRows = readSheet.getRows();
			for(int i = 1; i < 71; ++i){
				for(int j = 0; j < 6; ++j){
					Cell cell = readSheet.getCell(j, i);
					if(5 == j){
						optData[i - 1][0] = Double.parseDouble(cell.getContents());
					}else{
						sampleData[i - 1][j] = Double.parseDouble(cell.getContents());
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(fin != null){
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		BPNN bp = new BPNN(sampleSize, inputSize, outputSize, 20, 0.1);
		bp.Train(sampleData, optData);
		
		sampleSize = 81;
		sampleData = new double[sampleSize][inputSize];
		optData = new double[sampleSize][outputSize];
		try {
			fin = new FileInputStream("E:/subj_trainvalid-revised.xls");
			jxl.Workbook readwb = Workbook.getWorkbook(fin);
			Sheet readSheet = readwb.getSheet(0);
			int rsColums = readSheet.getColumns();
			int rsRows = readSheet.getRows();
			for(int i = 1; i < rsRows; ++i){
				for(int j = 0; j < 6; ++j){
					Cell cell = readSheet.getCell(j, i);
					if(5 == j){
						optData[i - 1][0] = Double.parseDouble(cell.getContents());
					}else{
						sampleData[i - 1][j] = Double.parseDouble(cell.getContents());
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(fin != null){
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		//以下为存储实验结果到excel文档中
		FileOutputStream fos;
		WritableWorkbook wwb = null;
		NumberFormat nf = new NumberFormat("#.##");
		WritableCellFormat wcf = new WritableCellFormat(nf);
		try {
			fos = new FileOutputStream("E:/partSamResult.xls");
			wwb = Workbook.createWorkbook(fos);
			WritableSheet ws = wwb.createSheet("Sheet1", 0);
			Label label = new Label(0, 0, "SampleSize:70; Neuron:20; LearningRate:0.1");
			ws.addCell(label);
			label = new Label(0, 1, "Desired Result");
			ws.addCell(label);
			label = new Label(1, 1, "Estimated Result");
			ws.addCell(label);
			jxl.write.Number number = null;
			for(int i = 0; i < 80; ++i){
				//System.out.println("Desired result: " + optData[i][0] + "; " + "Result: " + bp.getResult(sampleData[i])[0]);
				number = new jxl.write.Number(0, i + 2, optData[i][0], wcf);
				ws.addCell(number);
				number = new jxl.write.Number(1, i + 2, bp.getResult(sampleData[i])[0], wcf);
				ws.addCell(number);
			}
			wwb.write();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(wwb != null){
				try {
					wwb.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
	}
}
