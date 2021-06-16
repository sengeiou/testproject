import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICar, Car } from '../car.model';
import { CarService } from '../service/car.service';
import { IDog } from 'app/entities/dog/dog.model';
import { DogService } from 'app/entities/dog/service/dog.service';

@Component({
  selector: 'jhi-car-update',
  templateUrl: './car-update.component.html',
})
export class CarUpdateComponent implements OnInit {
  isSaving = false;

  dogsSharedCollection: IDog[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    title: [],
    date: [],
    type: [],
    dog: [],
  });

  constructor(
    protected carService: CarService,
    protected dogService: DogService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ car }) => {
      this.updateForm(car);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const car = this.createFromForm();
    if (car.id !== undefined) {
      this.subscribeToSaveResponse(this.carService.update(car));
    } else {
      this.subscribeToSaveResponse(this.carService.create(car));
    }
  }

  trackDogById(index: number, item: IDog): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICar>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(car: ICar): void {
    this.editForm.patchValue({
      id: car.id,
      name: car.name,
      title: car.title,
      date: car.date,
      type: car.type,
      dog: car.dog,
    });

    this.dogsSharedCollection = this.dogService.addDogToCollectionIfMissing(this.dogsSharedCollection, car.dog);
  }

  protected loadRelationshipsOptions(): void {
    this.dogService
      .query()
      .pipe(map((res: HttpResponse<IDog[]>) => res.body ?? []))
      .pipe(map((dogs: IDog[]) => this.dogService.addDogToCollectionIfMissing(dogs, this.editForm.get('dog')!.value)))
      .subscribe((dogs: IDog[]) => (this.dogsSharedCollection = dogs));
  }

  protected createFromForm(): ICar {
    return {
      ...new Car(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      title: this.editForm.get(['title'])!.value,
      date: this.editForm.get(['date'])!.value,
      type: this.editForm.get(['type'])!.value,
      dog: this.editForm.get(['dog'])!.value,
    };
  }
}
