package com.example.myfirstandroidapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerSource, spinnerDestination;
    private EditText editTextValue;
    private TextView textViewResult;
    private Button btnConvert;

    private HashMap<String, Double> lengthConversions = new HashMap<>();
    private HashMap<String, Double> weightConversions = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        spinnerSource = findViewById(R.id.spinnerSource);
        spinnerDestination = findViewById(R.id.spinnerDestination);
        editTextValue = findViewById(R.id.editTextValue);
        btnConvert = findViewById(R.id.btnConvert);
        textViewResult = findViewById(R.id.textViewResult);

        // Populate conversion data
        initializeConversionData();

        // Set up spinners with unit options
        String[] units = {"inch", "cm", "foot", "yard", "mile", "km", "pound", "kg", "ounce", "g", "ton", "Celsius", "Fahrenheit", "Kelvin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(adapter);
        spinnerDestination.setAdapter(adapter);

        // Convert button logic
        btnConvert.setOnClickListener(v -> performConversion());
    }

    private void initializeConversionData() {
        // Length conversions (to cm or km)
        lengthConversions.put("inch", 2.54);
        lengthConversions.put("foot", 30.48);
        lengthConversions.put("yard", 91.44);
        lengthConversions.put("mile", 160934.0); // Convert miles to cm
        lengthConversions.put("cm", 1.0); // Add cm as a base unit
        lengthConversions.put("km", 100000.0); // Convert km to cm

        // Weight conversions (to kg or g)
        weightConversions.put("pound", 0.453592);
        weightConversions.put("ounce", 28.3495);
        weightConversions.put("ton", 907.185);
        weightConversions.put("kg", 1.0); // Base unit
        weightConversions.put("g", 1000.0); // Convert kg to g
    }

    private void performConversion() {
        String sourceUnit = spinnerSource.getSelectedItem().toString();
        String destinationUnit = spinnerDestination.getSelectedItem().toString();
        String inputValue = editTextValue.getText().toString();

        // Validation: Check if input is empty
        if (inputValue.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse input value
        double value;
        try {
            value = Double.parseDouble(inputValue);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation: Prevent same unit conversion
        if (sourceUnit.equals(destinationUnit)) {
            textViewResult.setText("Same unit selected. No conversion needed.");
            return;
        }

        double result;
        if (lengthConversions.containsKey(sourceUnit) && lengthConversions.containsKey(destinationUnit)) {
            result = convertLength(value, sourceUnit, destinationUnit);
        } else if (weightConversions.containsKey(sourceUnit) && weightConversions.containsKey(destinationUnit)) {
            result = convertWeight(value, sourceUnit, destinationUnit);
        } else if (isTemperatureConversion(sourceUnit, destinationUnit)) {
            result = convertTemperature(value, sourceUnit, destinationUnit);
        } else {
            Toast.makeText(this, "Invalid conversion!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Display result
        textViewResult.setText("Converted Value: " + result + " " + destinationUnit);
    }

    private double convertLength(double value, String sourceUnit, String destinationUnit) {
        // Convert from source unit to cm
        double valueInCm = value * lengthConversions.get(sourceUnit);

        // Convert from cm to destination unit
        return valueInCm / lengthConversions.get(destinationUnit);
    }

    private double convertWeight(double value, String sourceUnit, String destinationUnit) {
        // Convert from source unit to kg
        double valueInKg = value * weightConversions.get(sourceUnit);

        // Convert from kg to destination unit
        return valueInKg / weightConversions.get(destinationUnit);
    }

    private boolean isTemperatureConversion(String sourceUnit, String destinationUnit) {
        return (sourceUnit.equals("Celsius") || sourceUnit.equals("Fahrenheit") || sourceUnit.equals("Kelvin"))
                && (destinationUnit.equals("Celsius") || destinationUnit.equals("Fahrenheit") || destinationUnit.equals("Kelvin"));
    }

    private double convertTemperature(double value, String sourceUnit, String destinationUnit) {
        if (sourceUnit.equals("Celsius") && destinationUnit.equals("Fahrenheit")) {
            return (value * 1.8) + 32;
        } else if (sourceUnit.equals("Fahrenheit") && destinationUnit.equals("Celsius")) {
            return (value - 32) / 1.8;
        } else if (sourceUnit.equals("Celsius") && destinationUnit.equals("Kelvin")) {
            return value + 273.15;
        } else if (sourceUnit.equals("Kelvin") && destinationUnit.equals("Celsius")) {
            return value - 273.15;
        } else if (sourceUnit.equals("Fahrenheit") && destinationUnit.equals("Kelvin")) {
            return (value - 32) / 1.8 + 273.15;
        } else if (sourceUnit.equals("Kelvin") && destinationUnit.equals("Fahrenheit")) {
            return (value - 273.15) * 1.8 + 32;
        }
        return value; // Default case (should never reach here)
    }
}
