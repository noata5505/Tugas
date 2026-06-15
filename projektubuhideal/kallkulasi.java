package com.mycompany.projektubuhideal;
public class kallkulasi {
    
    public double BMI(double tbCm, double bbKg){
        double tbMeter = tbCm / 100.0;
        return bbKg / (tbMeter * tbMeter);
    }
    
    public String kategori(int umur, String gender, double tb, double bb){
        double bmi = BMI(tb, bb);
        //Pria dewasa
        double batasBawah = 18.5;
        double batasAtas = 25.0;
        //Wanita Dewasa
        if(gender.equalsIgnoreCase("Wanita")){
            batasBawah = 18.0;
            batasAtas = 24.0;
        }
        //Umur
        if(umur < 18){
            batasBawah -= 1.0;
            batasAtas -= 1.0;
        } else if (umur > 50){
            batasBawah += 1.0;
            batasAtas += 2.0;
        }
        
        //Hasil
        if (bmi < (batasBawah - 1.5)){
            return "Low (Kurang banget)";
        } else if (bmi >= (batasBawah - 1.5) && bmi <= batasBawah){
            return "Medium (Kurang dikit)";
        } else if (bmi >= batasBawah && bmi <= batasAtas){
            return "Perfect (Badan ideal)";
        } else if (bmi > batasAtas && bmi < (batasAtas + 5.0)){
            return "High (Kelebihan dikit)";
        } else {
            return "Extreme (Kelebihan banget)";
        }
    }
}
