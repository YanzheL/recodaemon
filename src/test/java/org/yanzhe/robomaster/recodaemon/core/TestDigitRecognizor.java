//package org.yanzhe.robomaster.recodaemon.core;
//
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import org.yanzhe.robomaster.recodaemon.core.classifier.CnnDigitClassifier;
//import org.yanzhe.robomaster.recodaemon.utils.MnistReader;
//
//import java.util.Iterator;
//
//@Test(singleThreaded = true)
//public class TestDigitRecognizor {
//    private CnnDigitClassifier reco;
//  private MnistReader reader;
//
//  //    @BeforeMethod
//  TestDigitRecognizor() {
//      reco = new CnnDigitClassifier("mnist", "serve");
//    reader =
//        //        new MnistReader(
//        //            "data/t10k-labels-idx1-ubyte.idx1-ubyte",
//        // "data/t10k-images-idx3-ubyte.idx3-ubyte");
//        new MnistReader(
//            "data/train-labels-idx1-ubyte.idx1-ubyte", "data/train-images-idx3-ubyte.idx3-ubyte");
//  }
//
//  @DataProvider(name = "trainDt")
//  public Iterator<Object[]> provideMnistTrainData() {
//    return reader;
//  }
//
//  @Test(
//    dataProvider = "trainDt",
//    //    timeOut = 4,
//    successPercentage = 90
//  )
//  public void testRecognize(byte[] imgData, int label) {
//    Assert.assertEquals(reco.predict(imgData), label);
//  }
//
//  @AfterClass
//  public void close() {
//    reco.close();
//  }
//}
