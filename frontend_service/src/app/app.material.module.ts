import { NgModule } from "@angular/core";
import { AppComponent } from "./app.component";
import { MatButtonModule } from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatCardModule} from '@angular/material/card';
import {MatTabsModule} from '@angular/material/tabs';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatDividerModule} from '@angular/material/divider';
import {MatProgressSpinnerModule, MatSpinner} from '@angular/material/progress-spinner';

@NgModule({
    exports: 
    [
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        FormsModule,
        MatSnackBarModule,
        MatProgressBarModule,
        MatCardModule,
        MatTabsModule,
        MatSidenavModule,
        MatDividerModule,
        MatProgressSpinnerModule
    ],
    imports:
    [
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        FormsModule,
        MatSnackBarModule,
        MatProgressBarModule,
        MatCardModule,
        MatTabsModule,
        MatSidenavModule,
        MatDividerModule,
        MatProgressSpinnerModule
    ]

})
export class AppMaterialModule {}