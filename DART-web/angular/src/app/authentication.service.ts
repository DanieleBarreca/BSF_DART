import {of as observableOf, timer as observableTimer,  Observable } from 'rxjs';
import {catchError, map,  switchMap } from 'rxjs/operators';
import { Injectable, OnInit } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { HttpParams } from '@angular/common/http';
import { environment } from '../environments/environment';
import { Router } from '@angular/router';
import { v4 as uuid } from 'uuid';

export const userUrl = 'v1/user';

@Injectable()
export class AuthenticationService{
  user = null;
  group = null;
  token = null;
  queryToken=null;

  autoLoginPromise;
  private autoLoginSubscription;

  constructor(private http: HttpClient, private router: Router) {
    this.autoLoginPromise = observableTimer(500,10000).pipe(
      switchMap(() => this.refreshUserData())
    );
    this.autoLoginSubscription = this.autoLoginPromise.subscribe();
  }

  logout(){
    sessionStorage.removeItem('vcf-viewer-token');
    sessionStorage.removeItem('vcf-viewer-user');
    sessionStorage.removeItem('vcf-viewer-group');
    sessionStorage.removeItem('vcf-viewer-query-token');

    this.user = null;
    this.group = null;
    this.token = null;
    this.queryToken = null;

    if (this.autoLoginSubscription) {
      this.autoLoginSubscription.unsubscribe();
      this.autoLoginSubscription = null;
    }
  }

  login(username: string, password: string){
    this.logout();

    let authToken = btoa(username+":"+password);
    sessionStorage.setItem('vcf-viewer-token', authToken);
    sessionStorage.setItem('vcf-viewer-user',username);

    this.autoLoginSubscription = this.autoLoginPromise.subscribe();
  }

  private refreshUserData(){
    let authToken = sessionStorage.getItem('vcf-viewer-token');
    let username = sessionStorage.getItem('vcf-viewer-user');
    if (!authToken || !username){
      return observableOf(null);
    }

    let options = this.getOptionsInternal(authToken);

    return this.http.get(environment.server + userUrl, options).pipe(map(
      user => {
        if (user && user['userName'] && user['userName']== username){
          this.user = user;
          this.group = sessionStorage.getItem('vcf-viewer-group');
          this.queryToken = sessionStorage.getItem('vcf-viewer-query-token');

          if (!this.group || !Object.keys(user['permissions']).includes(this.group)){
            if (Object.keys(user['permissions']).length > 0){
              this.group = Object.keys(user['permissions'])[0];
              sessionStorage.setItem('vcf-viewer-group',this.group);
            }else{
              this.group = null;
              sessionStorage.removeItem('vcf-viewer-group');
            }
          }
          if (!this.queryToken && Object.keys(user['permissions']).includes(this.group) && user['permissions'][this.group]['publicUser']){
              sessionStorage.setItem('vcf-viewer-query-token',uuid());
              this.queryToken = sessionStorage.getItem('vcf-viewer-query-token');
          }

          if ( Object.keys(user['permissions']).includes(this.group) && !user['permissions'][this.group]['publicUser']){
            sessionStorage.removeItem('vcf-viewer-query-token');
            this.queryToken = null;
          }

          this.token = authToken;
          return user;
        }
      }
    ),catchError((e) => {
      console.log(e);
      this.logout();
      this.router.navigate(["login"])
      return observableOf(null);
    }),);
  }

  setUserGroup(group) {
    this.group = group;
    sessionStorage.setItem('vcf-viewer-group',group);
  }

  getUserGroup() {
    return this.group
  }

  getAuthToken() {
    return this.token;
  }

  getUser() {
    if (this.user!=null) {
      return this.user['userName'];
    }
    return null;
  }

  getUserWithDetails(){
    return this.user;
  }

  getPermissions() {
    return this.user['permissions'][this.group]
  }

  getUserGroups() {
    return Object.keys(this.user['permissions'])
  }

  getQueryToken(){
    return this.queryToken;
  }

  getOptions():MyHttpOptions {
    if (this.user) {
      return this.getOptionsInternal(this.token);
    }

    return null;
  }

  getOptionsForText():MyHttpOptions {

    let httpOptions = this.getOptions();
    httpOptions.addResponseType('text');

    return httpOptions;
  }


  getOptionsForFileUpload():MyHttpOptions {

    let httpHeaders = new HttpHeaders({
      'Authorization': 'Basic '+this.token,
    });

    let httpOptions = new MyHttpOptions(httpHeaders);
    httpOptions.addParam('userGroup', this.group);

    return httpOptions;
  }

  getOptionsForFile():MyHttpOptions {

    let httpOptions = this.getOptions();
    httpOptions.addResponseType('blob');
    httpOptions.addObserveResponse();

    return httpOptions;
  }

  private getOptionsInternal(authToken: string):MyHttpOptions {

    let httpHeaders = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic '+authToken,
    });

    let httpOptions = new MyHttpOptions(httpHeaders);
    httpOptions.addParam('userGroup', this.group);

    return httpOptions;
  }

}

export class MyHttpOptions {
  headers: HttpHeaders;
  withCredentials: true;
  params: HttpParams = new HttpParams();


  constructor(headers: HttpHeaders){
    this.headers = headers;
  }

  addParam(param: string, value: string){
    this.params = this.params.set(param, value);
  }

  appendParam(param: string, value: string){
    this.params = this.params.append(param, value);
  }

  addResponseType(responseType: string){
    this['responseType'] = responseType;
  }

  setContentType(contentType: string){
    this.headers = this.headers.set('Content-type',contentType);
  }

  addObserveResponse(){
    this['observe'] = 'response';
  }

}

