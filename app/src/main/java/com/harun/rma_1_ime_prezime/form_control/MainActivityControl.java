package com.harun.rma_1_ime_prezime.form_control;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.harun.rma_1_ime_prezime.databinding.ActivityMainBinding;
import com.harun.rma_1_ime_prezime.expression_parser.ExpressionParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivityControl {
    //All rights to Harun Jaganjac Software engineer of DCCS
    public static String headerText="Welcome to PersonalCalc".toUpperCase();

    private static EditText calcDisplay;
    private static boolean isSimpleMode;

    //Functions for seeting the main view --START
    public static void animateHeader(ActivityMainBinding binding) {
        binding.headerText.setText("");

        final int delay = 150;
        Handler handler = new Handler();
        final int[] index = {0};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (index[0] < headerText.length()) {
                    binding.headerText.append(String.valueOf(headerText.charAt(index[0])));
                    index[0]++;
                    handler.postDelayed(this, delay);
                } else {
                    handler.postDelayed(() -> {
                        binding.headerText.setText("");
                        index[0] = 0;
                        handler.post(this);
                    }, 2000);
                }
            }
        };

        handler.post(runnable);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);
        fadeIn.setRepeatMode(Animation.RESTART);
        fadeIn.setRepeatCount(Animation.INFINITE);

        Animation slideDown = new TranslateAnimation(0, 0, -100, 0);
        slideDown.setDuration(1000);
        slideDown.setRepeatMode(Animation.RESTART);
        slideDown.setRepeatCount(Animation.INFINITE);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(slideDown);

        binding.headerText.startAnimation(animationSet);
    }
    public static void setupMenu(ActivityMainBinding binding) {
        binding.sectionCalculator.setOnClickListener(view -> {
            selectSection(binding, true);
        });

        binding.sectionActions.setOnClickListener(view -> {
            selectSection(binding, false);
        });
    }

    private static void selectSection(ActivityMainBinding binding, boolean isCalculatorSelected) {
        if(binding.emptySectionMessage.getVisibility()==View.VISIBLE){
            binding.emptySectionMessage.setVisibility(View.GONE);
        }
        if (isCalculatorSelected) {
            binding.indicatorCalculator.setVisibility(View.VISIBLE);
            binding.indicatorActions.setVisibility(View.INVISIBLE);
            setupCalculatorUI(binding,"");
        } else {
            binding.indicatorCalculator.setVisibility(View.INVISIBLE);
            binding.indicatorActions.setVisibility(View.VISIBLE);
            setupHistoryListUI(binding);
        }

        Animation fade = new AlphaAnimation(0, 1);
        fade.setDuration(300);

        if (isCalculatorSelected) {
            binding.container.startAnimation(fade);
        } else {
            binding.container.startAnimation(fade);
        }
    }

    public static void setupCalculatorUI(ActivityMainBinding binding,String expression) {
        binding.container.removeAllViews();

        LinearLayout calculatorLayout = new LinearLayout(binding.getRoot().getContext());
        calculatorLayout.setOrientation(LinearLayout.VERTICAL);
        calculatorLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        calcDisplay = new EditText(binding.getRoot().getContext());
        if(!expression.isEmpty()){
            calcDisplay.setText(expression);
            Toast.makeText(calcDisplay.getContext(), "The expression "+expression,Toast.LENGTH_SHORT);
        }

        calcDisplay.setId(View.generateViewId());
        calcDisplay.setHint("0");
        calcDisplay.setTextSize(24);
        calcDisplay.setEnabled(false);
        calcDisplay.setBackgroundColor(Color.WHITE);
        calcDisplay.setTextColor(Color.parseColor("#FF6F00"));
        calcDisplay.setPadding(16,16,16,16);
        calculatorLayout.addView(calcDisplay);

        final LinearLayout[] buttonGrid = { generateButtonGrid(binding, true) };
        calculatorLayout.addView(buttonGrid[0]);

        Button switchButton = new Button(binding.getRoot().getContext());
        switchButton.setText("Switch to Complex Mode");
        switchButton.setOnClickListener(v -> {
            isSimpleMode = !isSimpleMode;
            switchButton.setText(isSimpleMode
                    ? "Switch to Complex Mode"
                    : "Switch to Simple Mode"
            );
            calculatorLayout.removeView(buttonGrid[0]);
            LinearLayout newGrid = generateButtonGrid(binding, isSimpleMode);
            calculatorLayout.addView(newGrid, 1);
            buttonGrid[0] = newGrid;
        });
        calculatorLayout.addView(switchButton);

        binding.container.addView(calculatorLayout);
    }


    public static void setupHistoryListUI(ActivityMainBinding binding) {
        Context ctx = binding.getRoot().getContext();
        FrameLayout container = binding.container;

        container.removeAllViews();

        LinearLayout historyLayout = new LinearLayout(ctx);
        historyLayout.setOrientation(LinearLayout.VERTICAL);
        historyLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        historyLayout.setPadding(16, 16, 16, 16);

        ScrollView scrollView = new ScrollView(ctx);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        LinearLayout listLayout = new LinearLayout(ctx);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        listLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        try {
            FileInputStream fis = ctx.openFileInput("calc_history.txt");
            byte[] buf = new byte[fis.available()];
            fis.read(buf);
            fis.close();

            String content = new String(buf).trim();
            if (content.isEmpty()) {
                TextView empty = new TextView(ctx);
                empty.setText("No history available.");
                empty.setTextSize(18);
                empty.setTextColor(Color.GRAY);
                empty.setPadding(8, 8, 8, 8);
                listLayout.addView(empty);
            } else {
                for (String line : content.split("\n")) {
                    String[] parts = line.split("\\|", 3);
                    String expr = parts.length > 0 ? parts[0] : "";
                    String res  = parts.length > 1 ? parts[1] : "";
                    String date = parts.length > 2 ? parts[2] : "";

                    LinearLayout item = new LinearLayout(ctx);
                    item.setOrientation(LinearLayout.VERTICAL);
                    item.setPadding(8, 8, 8, 8);
                    item.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    item.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    TextView tvExpr = new TextView(ctx);
                    tvExpr.setText("Expression: " + expr);
                    tvExpr.setTextSize(18);
                    tvExpr.setTypeface(null, Typeface.BOLD);
                    tvExpr.setTextColor(Color.BLACK);
                    item.addView(tvExpr);

                    TextView tvRes = new TextView(ctx);
                    tvRes.setText("Value = " + res);
                    tvRes.setTextSize(16);
                    tvRes.setTextColor(Color.DKGRAY);
                    item.addView(tvRes);

                    TextView tvDate = new TextView(ctx);
                    tvDate.setText("Date: " + date);
                    tvDate.setTextSize(12);
                    tvDate.setTextColor(Color.parseColor("#FF6F00"));
                    item.addView(tvDate);

                    item.setOnClickListener(v -> {
                        setupCalculatorUI(binding, expr);
                        binding.indicatorCalculator.setVisibility(View.VISIBLE);
                        binding.indicatorActions.setVisibility(View.INVISIBLE);
                    });

                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) item.getLayoutParams();
                    lp.setMargins(0, 0, 0, 16);
                    item.setLayoutParams(lp);

                    listLayout.addView(item);
                }
            }
        } catch (java.io.FileNotFoundException fnf) {
            TextView empty = new TextView(ctx);
            empty.setText("No history available.");
            empty.setTextSize(18);
            empty.setTextColor(Color.GRAY);
            empty.setPadding(8, 8, 8, 8);
            listLayout.addView(empty);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button downloadButton = new Button(ctx);
        downloadButton.setText("Download Report");
        downloadButton.setBackgroundColor(Color.parseColor("#FF6F00"));
        downloadButton.setTextColor(Color.WHITE);
        downloadButton.setPadding(16, 16, 16, 16);

        downloadButton.setOnClickListener(v -> {
            try {
                FileInputStream fis = ctx.openFileInput("calc_history.txt");
                byte[] buf = new byte[fis.available()];
                fis.read(buf);
                fis.close();

                String historyContent = new String(buf);

                StringBuilder htmlContent = new StringBuilder();
                htmlContent.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
                htmlContent.append("<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
                htmlContent.append("<title>Calculation History</title>\n");

                htmlContent.append("<style>\n");
                htmlContent.append("body { font-family: Arial, sans-serif; background-color: white; margin: 20px; color: #333; }\n");
                htmlContent.append("table { width: 100%; border-collapse: collapse; }\n");
                htmlContent.append("th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }\n");
                htmlContent.append("th { background-color: #FF6F00; color: white; }\n");
                htmlContent.append("td { background-color: #f9f9f9; }\n");
                htmlContent.append("footer { margin-top: 40px; text-align: center; font-size: 14px; color: #FF6F00; }\n");
                htmlContent.append("</style>\n");

                htmlContent.append("</head>\n<body>\n");
                htmlContent.append("<h1 style=\"color: #FF6F00; text-align: center;\">Calculation History</h1>\n");

                htmlContent.append("<table>\n<thead><tr><th>Expression</th><th>Result</th><th>Timestamp</th></tr></thead>\n<tbody>\n");


                String[] lines = historyContent.split("\n");
                for (String line : lines) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        String expression = parts[0];
                        String result = parts[1];
                        String timestamp = parts[2];
                        htmlContent.append("<tr><td>").append(expression).append("</td><td>").append(result).append("</td><td>").append(timestamp).append("</td></tr>\n");
                    }
                }

                htmlContent.append("</tbody>\n</table>\n</body>\n</html>");

                htmlContent.append("<footer style=\"margin-top: 40px; text-align: center; font-size: 14px; color: #888;\">\n");
                htmlContent.append("Provided by Harun Jaganjac @2025\n");
                htmlContent.append("</footer>\n");

                htmlContent.append("</body>\n</html>");

                String fileName = "calc_report_" + System.currentTimeMillis() + ".html";
                File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File reportFile = new File(downloadsFolder, fileName);

                FileOutputStream fos = new FileOutputStream(reportFile);
                fos.write(htmlContent.toString().getBytes());
                fos.close();


                Toast.makeText(ctx, "Report saved as HTML: " + fileName, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Failed to save report.", Toast.LENGTH_SHORT).show();
            }
        });


        historyLayout.addView(downloadButton);

        scrollView.addView(listLayout);
        historyLayout.addView(scrollView);
        container.addView(historyLayout);
    }


    private static LinearLayout generateButtonGrid(ActivityMainBinding binding, boolean isSimple) {
        Context ctx = binding.getRoot().getContext();
        LinearLayout grid = new LinearLayout(ctx);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        String[][] simpleButtons = {
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", "C", "=", "+","."}
        };

        String[][] complexButtons = {
                {"sin", "cos", "tan", "/"},
                {"log", "sqrt", "^", "*"},
                {"(", ")", "%", "-"},
                {"0", "C", "=", "+"," ."}
        };

        String[][] selectedButtons = isSimple ? simpleButtons : complexButtons;

        for (String[] row : selectedButtons) {
            LinearLayout rowLayout = new LinearLayout(ctx);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            for (String label : row) {
                Button btn = new Button(ctx);
                btn.setText(label);
                btn.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                ));

                btn.setOnClickListener(v -> {
                    String cur = calcDisplay.getText().toString();

                    switch (label) {
                        case "C":
                            calcDisplay.setText("");
                            break;

                        case "⌫":
                            if (!cur.isEmpty())
                                calcDisplay.setText(cur.substring(0, cur.length()-1));
                            break;

                        case "=":
                            try {
                                double result = new ExpressionParser().parse(cur);
                                String resStr = String.valueOf(result);

                                // timestamp
                                String ts = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                                ).format(new Date());

                                // append to file
                                String line = cur + "|" + resStr + "|" + ts + "\n";
                                try (FileOutputStream fos = ctx.openFileOutput(
                                        "calc_history.txt",
                                        Context.MODE_APPEND
                                )) {
                                    fos.write(line.getBytes());
                                }

                                calcDisplay.setText(resStr);
                            } catch (Exception e) {
                                calcDisplay.setText("Error");
                            }
                            break;

                        case ".":
                            int lastOp = Math.max(
                                    Math.max(cur.lastIndexOf('+'), cur.lastIndexOf('-')),
                                    Math.max(cur.lastIndexOf('*'), cur.lastIndexOf('/'))
                            );
                            String lastNum = lastOp >= 0 ? cur.substring(lastOp+1) : cur;
                            if (!lastNum.contains(".")) {
                                calcDisplay.append(".");
                            }
                            break;

                        case "sin": case "cos": case "tan":
                            calcDisplay.append(label + "(");
                            break;

                        case "log":
                            calcDisplay.append("log(");
                            break;

                        case "sqrt":
                            calcDisplay.append("sqrt(");
                            break;

                        case "^":
                        case "%":
                            calcDisplay.append(label);
                            break;

                        case "(":
                        case ")":
                            calcDisplay.append(label);
                            break;

                        default:
                            calcDisplay.append(label);
                    }
                });

                rowLayout.addView(btn);
            }
            grid.addView(rowLayout);
        }

        return grid;
    }
}

//Functions for seeting the main view --END

