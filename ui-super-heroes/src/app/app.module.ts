import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { FightListComponent } from './fight-list/fight-list.component';
import { FightComponent } from './fight/fight.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatDividerModule } from '@angular/material/divider';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { FightService } from './shared';
import { HttpClientModule } from '@angular/common/http';
import { MatGridListModule } from '@angular/material/grid-list';
import { AppComponentTimer } from './timer/app-component-timer';
import { CountdownModule } from 'ngx-countdown';
import { NotifyComponent } from './countdown/countdown.component';

@NgModule({
  declarations: [
    AppComponent,
    FightListComponent,
    FightComponent,
    AppComponentTimer,
    NotifyComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatDividerModule,
    MatCardModule,
    MatButtonModule,
    MatGridListModule,
    MatTableModule,
    CountdownModule,
  ],
  providers: [FightService],
  bootstrap: [AppComponent]
})
export class AppModule { }
