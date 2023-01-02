package Pensions;


import java.util.ArrayList;

public class PensionsEstimation {

    public static void main(String[] arr)throws Exception{

        PensionsEstimation pensionsEstimation = new PensionsEstimation();
        ArrayList<Double> results = pensionsEstimation.calAllPotentialPensions(0.02);

        Double total = 0.00;
        for(int i=0; i<results.size(); i++){
            Double result = results.get(i);
            total = total + result;
            System.out.printf("The accumulated " + i + "th year pension is: %.2f \r\n", result);
        }
        System.out.printf("The total accumulated pension at the retirement year is: %.2f", total);
    }

    public ArrayList<Double> calAllPotentialPensions(final double increaseRate){
        ArrayList<Double> results = new ArrayList<Double>();
        final double base = 14400 + 13000*2;
        final int totalYears = 20;

        for(int i=totalYears; i>=0; i--){
            results.add(this.calPensions(base, increaseRate, i));
        }
        return results;
    }

    public double calPensions(final double base, final double increaseRate, final int years){
        return base * Math.pow((1+increaseRate), years);
    }

}
