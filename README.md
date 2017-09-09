# Baking APP
[![Build Status](https://travis-ci.org/ngengs/android_Baking-Apps.svg?branch=development)](https://travis-ci.org/ngengs/android_Baking-Apps)
<p style="text-align:center"><img src="https://user-images.githubusercontent.com/5084276/30244338-3e5b4494-95e6-11e7-8682-c0108e09dbb7.png" style="width:100%;height:auto;max-height;200px"/></p>
Baking App is Android application that will allow **Udacity**’s resident baker-in-chief, Miriam, to share her recipes with the world. You will create an app that will allow a user to select a recipe and see video-guided steps for how to complete it.
Yes this application is for **Udacity Associate Android Developer FastTrack program**


## Feature
* This app showcases responsive UI both for phone and tablet devices.
* Show a list of recipes
* Show a list of steps involved and a video icon and thumbnail if the step has a video
* Show a Card containing the ingredients of the recipe
* Use Exoplayer to show videos of steps involved in a recipe.
* Widget to show recipe ingredients on home screen.
* UI and Intents Test with Espresso

## Preview
* Phone
<p style="text-align:center">
<img src="https://user-images.githubusercontent.com/5084276/30244186-a3dc2ee4-95e3-11e7-8548-c523de7b7d6a.png" style="width:100%;height:auto"/>
<img src="https://user-images.githubusercontent.com/5084276/30244185-a3db7d96-95e3-11e7-9563-6d12b2f34431.png" style="width:100%;height:auto"/>
<img src="https://user-images.githubusercontent.com/5084276/30244187-a3df5560-95e3-11e7-940a-20814513dc31.png" style="width:100%;height:auto"/>
<img src="https://user-images.githubusercontent.com/5084276/30244189-a3e230be-95e3-11e7-888f-e159854a4847.png" style="width:100%;height:auto"/>
<img src="https://user-images.githubusercontent.com/5084276/30244190-a3e69e60-95e3-11e7-8fb7-559549597840.png" style="width:100%;height:auto"/>
<img src="https://user-images.githubusercontent.com/5084276/30244188-a3e19942-95e3-11e7-9995-d0acc10938b1.png" style="width:100%;height:auto"/>
</p>
* Tablet
<p style="text-align:center">
<img src="https://user-images.githubusercontent.com/5084276/30244191-a41412d2-95e3-11e7-911d-1fada7f5d85d.png" style="width:100%;height:auto"/>
<img src="https://user-images.githubusercontent.com/5084276/30244192-a413ef46-95e3-11e7-8b92-8755dbd777ae.png" style="width:100%;height:auto"/>
</p>

## Project Overview
You will productionize an app, taking it from a functional state to a production-ready state. This will involve finding and handling error cases, adding accessibility features, allowing for localization, adding a widget, and adding a library.

## Why this Project?
As a working Android developer, you often have to create and implement apps where you are responsible for designing and planning the steps you need to take to create a production-ready app. Unlike Popular Movies where we gave you an implementation guide, it will be up to you to figure things out for the Baking App.

## What Will I Learn?
In this project you will:
* Use MediaPlayer/Exoplayer to display videos.
* Handle error cases in Android.
* Add a widget to your app experience.
* Leverage a third-party library in your app.
* Use Fragments to create a responsive design that works on phones and tablets.

## Rubric

### General App Usage
- [-] App should display recipes from provided network resource.
- [-] App should allow navigation between individual recipes and recipe steps.
- [-] App uses RecyclerView and can handle recipe steps that include videos or images.
- [-] App conforms to common standards found in the Android Nanodegree General Project Guidelines.

### Components and Libraries
- [-] Application uses Master Detail Flow to display recipe steps and navigation between them.
- [-] Application uses Exoplayer to display videos.
- [-] Application properly initializes and releases video assets when appropriate.
- [-] Application should properly retrieve media assets from the provided network links. It should properly handle network requests.
- [-] Application makes use of Espresso to test aspects of the UI.
- [-] Application sensibly utilizes a third-party library to enhance the app's features. That could be helper library to interface with Content Providers if you choose to store the recipes, a UI binding library to avoid writing findViewById a bunch of times, or something similar.

### Homescreen Widget
- [-] Application has a companion homescreen widget.
- [-] Widget displays ingredient list for desired recipe.


## Author
**Rizky Kharisma** (https://github.com/ngengs)

## Copyright and License
Copyright 2017 Rizky Kharisma
Code released under the MIT License. Copy of LICENSE file can found [HERE](../blob/master/LICENSE)
