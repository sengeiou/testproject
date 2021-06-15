import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICard, Card } from '../card.model';
import { CardService } from '../service/card.service';
import { IDog } from 'app/entities/dog/dog.model';
import { DogService } from 'app/entities/dog/service/dog.service';

@Component({
  selector: 'jhi-card-update',
  templateUrl: './card-update.component.html',
})
export class CardUpdateComponent implements OnInit {
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
    protected cardService: CardService,
    protected dogService: DogService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ card }) => {
      this.updateForm(card);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const card = this.createFromForm();
    if (card.id !== undefined) {
      this.subscribeToSaveResponse(this.cardService.update(card));
    } else {
      this.subscribeToSaveResponse(this.cardService.create(card));
    }
  }

  trackDogById(index: number, item: IDog): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICard>>): void {
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

  protected updateForm(card: ICard): void {
    this.editForm.patchValue({
      id: card.id,
      name: card.name,
      title: card.title,
      date: card.date,
      type: card.type,
      dog: card.dog,
    });

    this.dogsSharedCollection = this.dogService.addDogToCollectionIfMissing(this.dogsSharedCollection, card.dog);
  }

  protected loadRelationshipsOptions(): void {
    this.dogService
      .query()
      .pipe(map((res: HttpResponse<IDog[]>) => res.body ?? []))
      .pipe(map((dogs: IDog[]) => this.dogService.addDogToCollectionIfMissing(dogs, this.editForm.get('dog')!.value)))
      .subscribe((dogs: IDog[]) => (this.dogsSharedCollection = dogs));
  }

  protected createFromForm(): ICard {
    return {
      ...new Card(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      title: this.editForm.get(['title'])!.value,
      date: this.editForm.get(['date'])!.value,
      type: this.editForm.get(['type'])!.value,
      dog: this.editForm.get(['dog'])!.value,
    };
  }
}
