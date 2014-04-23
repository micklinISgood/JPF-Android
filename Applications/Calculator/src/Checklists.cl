// name : condition => verify_list

[Mappings]
toast : android.widget.Toast.show()V, main;


[Checklists]

buttonEqualsClick: buttonclick, calculate, !showError =>  updateDisplay;
buttonEqualsClickError: buttonclick, calculate, showError => toast, clearDisplay;

buttonNextClick: buttonclick, startScientificActivity => saveState, createScientificActivity, setupGUI, restoreState;
buttonNext2Click: buttonclick, startSimpleActivity => saveState, createSimpleActivity, setupGUI, restoreState;


//rotate : 

//home : 




  