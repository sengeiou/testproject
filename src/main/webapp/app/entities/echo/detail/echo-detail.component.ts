import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEcho } from '../echo.model';

@Component({
  selector: 'jhi-echo-detail',
  templateUrl: './echo-detail.component.html',
})
export class EchoDetailComponent implements OnInit {
  echo: IEcho | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ echo }) => {
      this.echo = echo;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
