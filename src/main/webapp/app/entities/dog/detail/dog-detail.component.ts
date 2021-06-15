import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDog } from '../dog.model';

@Component({
  selector: 'jhi-dog-detail',
  templateUrl: './dog-detail.component.html',
})
export class DogDetailComponent implements OnInit {
  dog: IDog | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dog }) => {
      this.dog = dog;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
