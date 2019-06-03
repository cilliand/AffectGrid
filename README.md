# AffectGrid
A pretty simple library that allows gathering of user ratings on an Affect Grid.
   
1. [What does it do?](#what-does-it-do)
2. [How do I include it?](#how-do-i-include-it)

# What does it do?
The grid allows for highlighting the axes:<br/>
<img src="screenshots/1.jpg?raw=false" height="500" alt="In-app screenshot of Affect Grid with Axes Visible">

Hiding them completely:<br/>
<img src="screenshots/2.jpg?raw=false" height="500" alt="In-app screenshot of Affect Grid with Axes Hidden">

And having more or less fine-grained feedback:<br/>
<img src="screenshots/3.jpg?raw=false" height="500" alt="In-app screenshot of Affect Grid sized 20 by 20 and axes hidden">

The ratings can be gathered as raw values or as normalized values. 

The origin of the grid, and therefore the selected values can be switched between being the upper righthand corner or the center of the grid.

# How do I include it?
The library is available on jcenter(), to include it use:

```
repositories {
    jcenter()
}
```

and add the following to your dependencies:

```
dependencies {
  implementation 'com.cilliandudley.affectgrid:affectgrid:1.0.4'
}
```



