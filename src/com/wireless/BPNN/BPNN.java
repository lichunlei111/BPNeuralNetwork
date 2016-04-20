package com.wireless.BPNN;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class BPNN {
	/*
	 * Input Data 
	 */
	private double[] iptData;
	/*
	 * Hidden Layer Input Data
	 */
	private double[] hidInput;
	/*
	 * Hidden Layer Output Data
	 */
	private double[] hidOutput;
	/*
	 * Hidden Layer Threshold
	 */
	private double[] hidThd;
	/*
	 * Outlayer Input Data
	 */
	private double[] outLayerInput;
	/*
	 * Final Output Data
	 */
	private double[] optData;
	/*
	 * Outlayer Threshold
	 */
	private double[] optThd;
	/*
	 * Desired Output Data
	 */
	private double[] desiredOutput;
	/*
	 * Weight matrix from input layer to hidden layer
	 */
	private double[][] iptHidWeights;
	/*
	 * Weight matrix from hidden layer to out layer
	 */
	private double[][] hidOptWeights;
	/*
	 * Learning rate
	 */
	private double eta;
	/*
	 * Study times
	 */
	private double times = 5000;
	/*
	 * Delta of outlayer
	 */
	private double[] deltaOpt;
	/*
	 * Delta of hidden layer
	 */
	private double[] deltaHid;
	/*
	 * Output error
	 */
	private double[] sigOptErr;
	/*
	 * Min value of input data
	 */
	double[] minIn;
	/*
	 * Max value of input data
	 */
	double[] maxIn;
	/*
	 * Min value of output data
	 */
	double[] minOut;
	/*
	 * Max value of output data
	 */
	double[] maxOut;
	
	/*
	 * BPNN constructor function
	 */
	public BPNN(int sampleSize, int iptSize, int optSize, int hidNeuralNum, double eta){
		this.iptData = new double[iptSize];
		this.iptHidWeights = new double[iptSize][hidNeuralNum];
		this.hidOptWeights = new double[hidNeuralNum][optSize];
		this.optData = new double[optSize];
		this.desiredOutput = new double[optSize];
		this.hidInput = new double[hidNeuralNum];
		this.hidOutput = new double[hidNeuralNum];
		this.hidThd = new double[hidNeuralNum];
		this.outLayerInput = new double[optSize];
		this.optThd = new double[optSize];
		this.deltaOpt = new double[optSize];
		this.deltaHid = new double[hidNeuralNum];
		this.sigOptErr = new double[optSize];
		this.minIn = new double[iptSize];
		this.maxIn = new double[iptSize];
		this.minOut = new double[optSize];
		this.maxOut = new double[optSize];
		this.eta = eta;
	}
	
	/*
	 * BPNN train function
	 */
	public void Train(double[][] sampleData, double[][] optData){
		int number = 0;
		InitBPNN();
		InitData(sampleData, optData);
		double err;
		do{
			err = 0.0;
			for(int i = 0; i < sampleData.length; ++i){
				this.iptData = sampleData[i];
				this.desiredOutput = optData[i];
				Forward();
				err += CalcErr();
				BackProp();
			}
			//err /= sampleData.length;
			//System.out.println(err);
			++number;
		}while(number < times || err > 0.001);
		restoreData(sampleData, optData);
		storeTrainModel();
		System.out.println("The times of training is: " + number);
		System.out.println("BPNN training is completed!");
	}
	
	/*
	 * BPNN train function
	 */
	public void Train(){
		FileInputStream fin = null;
		Cell cell = null;
		Sheet readSheet = null;
		int rsColums;
		int rsRows;
		try {
			fin = new FileInputStream("E:/AllSamModel.xls");
			jxl.Workbook readwb = Workbook.getWorkbook(fin);
			//Read input layer 2 hidden layer weights
			readSheet = readwb.getSheet(0);
			rsColums = readSheet.getColumns();
			rsRows = readSheet.getRows();
			for(int i = 1; i < rsRows; ++i){
				for(int j = 1; j < rsColums; ++j){
					cell = readSheet.getCell(j, i);
					iptHidWeights[i - 1][j - 1] = Double.parseDouble(cell.getContents());
				}
			}
			//Read hidden layer 2 output layer weights
			readSheet = readwb.getSheet(1);
			rsColums = readSheet.getColumns();
			rsRows = readSheet.getRows();
			for(int i = 1; i < rsRows; ++i){
				for(int j = 1; j < rsColums; ++j){
					cell = readSheet.getCell(j, i);
					hidOptWeights[i - 1][j - 1] = Double.parseDouble(cell.getContents());
				}
			}
			//Read hidden layer threshold value
			readSheet = readwb.getSheet(2);
			rsColums = readSheet.getColumns();
			for(int j = 0; j < rsColums; ++j){
				cell = readSheet.getCell(j, 1);
				hidThd[j] = Double.parseDouble(cell.getContents());
			}
			//Read hidden layer threshold value
			readSheet = readwb.getSheet(3);
			rsColums = readSheet.getColumns();
			for(int j = 0; j < rsColums; ++j){
				cell = readSheet.getCell(j, 1);
				optThd[j] = Double.parseDouble(cell.getContents());
			}
			//Read the min value of input data
			readSheet = readwb.getSheet(4);
			rsColums = readSheet.getColumns();
			for(int j = 0; j < rsColums; ++j){
				cell = readSheet.getCell(j, 1);
				minIn[j] = Double.parseDouble(cell.getContents());
			}
			//Read the max value of input data
			readSheet = readwb.getSheet(5);
			rsColums = readSheet.getColumns();
			for(int j = 0; j < rsColums; ++j){
				cell = readSheet.getCell(j, 1);
				maxIn[j] = Double.parseDouble(cell.getContents());
			}
			//Read the min value of output data
			readSheet = readwb.getSheet(6);
			rsColums = readSheet.getColumns();
			for(int j = 0; j < rsColums; ++j){
				cell = readSheet.getCell(j, 1);
				minOut[j] = Double.parseDouble(cell.getContents());
			}
			//Read the max value of output data
			readSheet = readwb.getSheet(7);
			rsColums = readSheet.getColumns();
			for(int j = 0; j < rsColums; ++j){
				cell = readSheet.getCell(j, 1);
				maxOut[j] = Double.parseDouble(cell.getContents());
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
		System.out.println("BPNN training is completed!");
	}
	
	/*
	 * Strore the train model
	 */
	private void storeTrainModel(){
		FileOutputStream fos;
		WritableWorkbook wwb = null;
		NumberFormat nf = new NumberFormat("#.##");
		WritableCellFormat wcf = new WritableCellFormat(nf);
		jxl.write.Number number = null;
		Label label = null;
		WritableSheet ws = null;
		try {
			fos = new FileOutputStream("E:/TrainModel.xls");
			wwb = Workbook.createWorkbook(fos);
			
			ws = wwb.createSheet("IptHidWeights", 0);
			for(int i = 1; i <= iptHidWeights[0].length; ++i){
				label = new Label(i, 0, "HidLayer" + i);
				ws.addCell(label);
			}
			for(int i = 1; i <= iptHidWeights.length; ++i){
				label = new Label(0, i, "InputLayer" + i);
				ws.addCell(label);
				for(int j = 1; j <= iptHidWeights[i - 1].length; ++j){
					number = new jxl.write.Number(j, i, iptHidWeights[i - 1][j - 1], wcf);
					ws.addCell(number);
				}	
			}
			
			ws = wwb.createSheet("HidOptWeights", 1);
			for(int i = 1; i <= hidOptWeights[0].length; ++i){
				label = new Label(i, 0, "OutputLayer" + i);
				ws.addCell(label);
			}
			for(int i = 1; i <= hidOptWeights.length; ++i){
				label = new Label(0, i, "HiddenLayer" + i);
				ws.addCell(label);
				for(int j = 1; j <= hidOptWeights[i - 1].length; ++j){
					number = new jxl.write.Number(j, i, hidOptWeights[i - 1][j - 1], wcf);
					ws.addCell(number);
				}
			}
			
			ws = wwb.createSheet("HidThd", 2);
			for(int i = 0; i < hidThd.length; ++i){
				label = new Label(i, 0, "HiddenLayer" + i);
				ws.addCell(label);
				number = new jxl.write.Number(i, 1, hidThd[i], wcf);
				ws.addCell(number);
			}
			
			ws = wwb.createSheet("OptThd", 3);
			for(int i = 0; i < optThd.length; ++i){
				label = new Label(i, 0, "OutputLayer" + i);
				ws.addCell(label);
				number = new jxl.write.Number(i, 1, optThd[i], wcf);
				ws.addCell(number);
			}
			
			ws = wwb.createSheet("MinIn", 4);
			for(int i = 0; i < minIn.length; ++i){
				label = new Label(i, 0, "Prams" + (i + 1));
				ws.addCell(label);
				number = new jxl.write.Number(i, 1, minIn[i], wcf);
				ws.addCell(number);
			}
			
			ws = wwb.createSheet("MaxIn", 5);
			for(int i = 0; i < maxIn.length; ++i){
				label = new Label(i, 0, "Prams" + (i + 1));
				ws.addCell(label);
				number = new jxl.write.Number(i, 1, maxIn[i], wcf);
				ws.addCell(number);
			}
			
			ws = wwb.createSheet("MinOut", 6);
			for(int i = 0; i < minOut.length; ++i){
				label = new Label(i, 0, "Result" + (i + 1));
				ws.addCell(label);
				number = new jxl.write.Number(i, 1, minOut[i], wcf);
				ws.addCell(number);
			}
			
			ws = wwb.createSheet("MaxOut", 7);
			for(int i = 0; i < maxOut.length; ++i){
				label = new Label(i, 0, "Result" + (i + 1));
				ws.addCell(label);
				number = new jxl.write.Number(i, 1, maxOut[i], wcf);
				ws.addCell(number);
			}
			wwb.write();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(wwb != null){
				try {
					wwb.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void InitData(double[][] iptData, double[][] optData){
		for(int i = 0; i < iptData.length; ++i){
			for(int j = 0; j < iptData[i].length; ++j){
				minIn[j] = minIn[j] < iptData[i][j] ? minIn[j] : iptData[i][j];
				maxIn[j] = maxIn[j] > iptData[i][j] ? maxIn[j] : iptData[i][j];
			}
		}
		for(int i = 0; i < optData.length; ++i){
			for(int j = 0; j < optData[i].length; ++j){
				minOut[j] = minOut[j] < optData[i][j] ? minOut[j] : optData[i][j];
				maxOut[j] = maxOut[j] > optData[i][j] ? maxOut[j] : optData[i][j];
			}
		}
		for(int i = 0; i < iptData.length; ++i){
			for(int j = 0; j < iptData[i].length; ++j){
				iptData[i][j] = (iptData[i][j] - minIn[j] + 1) / (maxIn[j] - minIn[j] + 1);
			}
		}
		for(int i = 0; i < optData.length; ++i){
			for(int j = 0; j < optData[i].length; ++j){
				optData[i][j] = (optData[i][j] - minOut[j] + 1) / (maxOut[j] - minOut[j] + 1);
			}
		}
	}
	
	/*
	 * BPNN initiation function
	 */
	private void InitBPNN(){
		for(int i = 0; i < iptHidWeights.length; ++i){
			for(int j = 0; j < hidInput.length; ++j){
				iptHidWeights[i][j] = (Math.random() - 0.5) * 2;
			}
		}
		for(int i = 0; i < hidOptWeights.length; ++i){
			for(int j = 0; j < optData.length; ++j){
				hidOptWeights[i][j] = (Math.random() - 0.5) * 2;
			}
			hidThd[i] = Math.random() * 2 - 1;
		}
		for(int i = 0; i < optThd.length; ++i){
			optThd[i] = Math.random() * 2 - 1;
		}
		for(int i = 0; i < minIn.length; ++i){
			minIn[i] = 100;
			maxIn[i] = -100;
		}
		for(int i = 0; i < minOut.length; ++i){
			minOut[i] = 100;
			maxOut[i] = -100;
		}
	}
	
	private void restoreData(double[][] sampleData, double[][] optData){
		for(int i = 0; i < sampleData.length; ++i){
			for(int j = 0; j < sampleData[i].length; ++j){
				sampleData[i][j] = sampleData[i][j] * (maxIn[j] - minIn[j] + 1) + minIn[j] - 1;
			}
		}
		for(int i = 0; i < optData.length; ++i){
			for(int j = 0; j < optData[i].length; ++j){
				optData[i][j] = optData[i][j] * (maxOut[j] - minOut[j] + 1) + minOut[j] - 1;
			}
		}
	}
	
	/*
	 * Sigmoid activation function
	 */
	private void Sigmoid(double[] input, double[] output){
		for(int i = 0; i < input.length; ++i){
			output[i] = 1.0 / (1.0 + Math.exp(-input[i]));
		}
	}
	
	/*
	 * Forward propagation main function
	 */
	private void Forward(){
		Forward(iptData, hidInput, iptHidWeights, hidThd);
		Sigmoid(hidInput, hidOutput);
		Forward(hidOutput, outLayerInput, hidOptWeights, optThd);
		Sigmoid(outLayerInput, optData);
	}
	
	/*
	 * Forward propagation
	 */
	private void Forward(double[] input, double[] output, double[][] weights, double[] threshold){
		for(int i = 0; i < output.length; ++i){
			double sum = 0;
			for(int j = 0; j < input.length; ++j){
				sum += (input[j] * weights[j][i]);
			}
			output[i] = sum + threshold[i];
		}
	}
	
	/*
	 * Back propagation
	 */
	private void BackProp(){
		CalDeltaOpt();
		AdHidOptWeights();
		AdOutlayerThd();
		CalDeltaHid();
		AdIptHidWeights();
		AdHiddenlayerThd();
	}
	
	/*
	 * Adjust Weight matrix of hidden layer to output layer
	 */
	private void AdHidOptWeights(){
		for(int i = 0; i < hidOptWeights.length; ++i){
			for(int j = 0; j < outLayerInput.length; ++j){
				hidOptWeights[i][j] += (eta * deltaOpt[j] * hidOutput[i]);
			}
		}
	}
	
	/*
	 * Adjust Outlayer Threshold
	 */
	private void AdOutlayerThd(){
		for(int i = 0; i < optThd.length; ++i){
			optThd[i] += eta * deltaOpt[i];
		}
	}
	
	/*
	 * Adjust Weight matrix of input layer to hidden layer
	 */
	private void AdIptHidWeights(){
		for(int i = 0; i < iptData.length; ++i){
			for(int j = 0; j < hidInput.length; ++j){
				iptHidWeights[i][j] += (eta * deltaHid[j] * iptData[i]);
			}
		}
	}
	
	/*
	 * Adjust Outlayer Threshold
	 */
	private void AdHiddenlayerThd(){
		for(int i = 0; i < hidThd.length; ++i){
			hidThd[i] += eta * deltaHid[i];
		}
	}
	
	/*
	 * Calculate output layer delta
	 */
	private void CalDeltaOpt(){
		for(int i = 0; i < optData.length; ++i){
			deltaOpt[i] = sigOptErr[i] * optData[i] * (1.0 - optData[i]);	
		}
	}
	
	/*
	 * Calculate hidden layer delta
	 */
	private void CalDeltaHid(){
		double sum;
		for(int i = 0; i < deltaHid.length; ++i){
			sum = 0;
			for(int j = 0; j < deltaOpt.length; ++j){
				sum += deltaOpt[j] * hidOptWeights[i][j];
			}
			deltaHid[i] = sum * hidOutput[i] * (1.0 - hidOutput[i]);
		}
	}
	
	/*
	 * Calculate output error
	 */
	private double CalcErr(){
		double err = 0.0;
		for(int i = 0; i < optData.length; ++i){
			sigOptErr[i] = desiredOutput[i] - optData[i];
			err += Math.pow(sigOptErr[i], 2.0);
		}
		return err / (2 * optData.length);
	}
	
	/*
	 * Estimate result
	 */
	public double[] getResult(double[] input){
		this.iptData = input;
		for(int i = 0; i < iptData.length; ++i){
			iptData[i] = (iptData[i] - minIn[i] + 1) / (maxIn[i] - minIn[i] + 1);
		}
		Forward();
		optData[0] = optData[0] * (maxOut[0] - minOut[0] + 1) + minOut[0] - 1;
		return optData;
	}
}