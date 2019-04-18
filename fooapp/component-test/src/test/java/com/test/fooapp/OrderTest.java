package com.test.fooapp;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
  plugin = {"pretty", "html:target/cucumber"},
  features = {"src/test/resources/com/test/fooapp/Echo.feature"}
)
public class OrderTest {}
