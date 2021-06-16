import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAuthor, Author } from '../author.model';
import { AuthorService } from '../service/author.service';
import { IDog } from 'app/entities/dog/dog.model';
import { DogService } from 'app/entities/dog/service/dog.service';
import { IEcho } from 'app/entities/echo/echo.model';
import { EchoService } from 'app/entities/echo/service/echo.service';

@Component({
  selector: 'jhi-author-update',
  templateUrl: './author-update.component.html',
})
export class AuthorUpdateComponent implements OnInit {
  isSaving = false;

  dogsCollection: IDog[] = [];
  echoesSharedCollection: IEcho[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    firstName: [],
    lastName: [],
    type: [],
    dog: [],
    echoes: [],
  });

  constructor(
    protected authorService: AuthorService,
    protected dogService: DogService,
    protected echoService: EchoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ author }) => {
      this.updateForm(author);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const author = this.createFromForm();
    if (author.id !== undefined) {
      this.subscribeToSaveResponse(this.authorService.update(author));
    } else {
      this.subscribeToSaveResponse(this.authorService.create(author));
    }
  }

  trackDogById(index: number, item: IDog): number {
    return item.id!;
  }

  trackEchoById(index: number, item: IEcho): number {
    return item.id!;
  }

  getSelectedEcho(option: IEcho, selectedVals?: IEcho[]): IEcho {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAuthor>>): void {
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

  protected updateForm(author: IAuthor): void {
    this.editForm.patchValue({
      id: author.id,
      name: author.name,
      firstName: author.firstName,
      lastName: author.lastName,
      type: author.type,
      dog: author.dog,
      echoes: author.echoes,
    });

    this.dogsCollection = this.dogService.addDogToCollectionIfMissing(this.dogsCollection, author.dog);
    this.echoesSharedCollection = this.echoService.addEchoToCollectionIfMissing(this.echoesSharedCollection, ...(author.echoes ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.dogService
      .query({ filter: 'author-is-null' })
      .pipe(map((res: HttpResponse<IDog[]>) => res.body ?? []))
      .pipe(map((dogs: IDog[]) => this.dogService.addDogToCollectionIfMissing(dogs, this.editForm.get('dog')!.value)))
      .subscribe((dogs: IDog[]) => (this.dogsCollection = dogs));

    this.echoService
      .query()
      .pipe(map((res: HttpResponse<IEcho[]>) => res.body ?? []))
      .pipe(map((echoes: IEcho[]) => this.echoService.addEchoToCollectionIfMissing(echoes, ...(this.editForm.get('echoes')!.value ?? []))))
      .subscribe((echoes: IEcho[]) => (this.echoesSharedCollection = echoes));
  }

  protected createFromForm(): IAuthor {
    return {
      ...new Author(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      type: this.editForm.get(['type'])!.value,
      dog: this.editForm.get(['dog'])!.value,
      echoes: this.editForm.get(['echoes'])!.value,
    };
  }
}
