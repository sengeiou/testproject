import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IDog, Dog } from '../dog.model';
import { DogService } from '../service/dog.service';
import { IActor } from 'app/entities/actor/actor.model';
import { ActorService } from 'app/entities/actor/service/actor.service';

@Component({
  selector: 'jhi-dog-update',
  templateUrl: './dog-update.component.html',
})
export class DogUpdateComponent implements OnInit {
  isSaving = false;

  actorsSharedCollection: IActor[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    description: [],
    created: [],
    actor: [],
  });

  constructor(
    protected dogService: DogService,
    protected actorService: ActorService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dog }) => {
      this.updateForm(dog);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dog = this.createFromForm();
    if (dog.id !== undefined) {
      this.subscribeToSaveResponse(this.dogService.update(dog));
    } else {
      this.subscribeToSaveResponse(this.dogService.create(dog));
    }
  }

  trackActorById(index: number, item: IActor): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDog>>): void {
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

  protected updateForm(dog: IDog): void {
    this.editForm.patchValue({
      id: dog.id,
      name: dog.name,
      description: dog.description,
      created: dog.created,
      actor: dog.actor,
    });

    this.actorsSharedCollection = this.actorService.addActorToCollectionIfMissing(this.actorsSharedCollection, dog.actor);
  }

  protected loadRelationshipsOptions(): void {
    this.actorService
      .query()
      .pipe(map((res: HttpResponse<IActor[]>) => res.body ?? []))
      .pipe(map((actors: IActor[]) => this.actorService.addActorToCollectionIfMissing(actors, this.editForm.get('actor')!.value)))
      .subscribe((actors: IActor[]) => (this.actorsSharedCollection = actors));
  }

  protected createFromForm(): IDog {
    return {
      ...new Dog(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      created: this.editForm.get(['created'])!.value,
      actor: this.editForm.get(['actor'])!.value,
    };
  }
}
