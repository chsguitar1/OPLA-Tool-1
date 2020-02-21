import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {OptimizationDto} from "./optimization-dto";
import {MatStepper} from "@angular/material/stepper";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, AfterViewInit {
  title = 'static';
  isLinear = false;
  generalFormGroup: FormGroup;
  executionFormGroup: FormGroup;
  patternFormGroup: FormGroup;
  resultsFormGroup: FormGroup;
  experimentsFormGroup: FormGroup;
  logsFormGroup: FormGroup;
  optimizationDto: OptimizationDto = new OptimizationDto();
  @ViewChild('stepper', {static: true}) stepper: MatStepper;

  constructor(private _formBuilder: FormBuilder) {
  }

  ngAfterViewInit(): void {
    this.stepper.selectedIndex = 5;
  }

  ngOnInit() {
    this.generalFormGroup = this._formBuilder.group({
      generalCtrl: ['', Validators.required]
    });
    this.executionFormGroup = this._formBuilder.group({
      executionCtrl: ['', Validators.required]
    });
    this.patternFormGroup = this._formBuilder.group({
      patternCtrl: ['', Validators.required]
    });
    this.resultsFormGroup = this._formBuilder.group({
      resultsCtrl: ['', Validators.required]
    });
    this.experimentsFormGroup = this._formBuilder.group({
      experimentsCtrl: ['', Validators.required]
    });
    this.logsFormGroup = this._formBuilder.group({
      logsCtrl: ['', Validators.required]
    });
  }
}