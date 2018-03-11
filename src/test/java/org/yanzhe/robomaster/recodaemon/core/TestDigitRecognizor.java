package org.yanzhe.robomaster.recodaemon.core;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.yanzhe.robomaster.recodaemon.utils.MnistReader;

import java.util.Iterator;

@Test(singleThreaded = true)
public class TestDigitRecognizor {
  private DigitRecognitor reco;
  private MnistReader reader;

  //    @BeforeMethod
  TestDigitRecognizor() {
    reco = new DigitRecognitor("mnist", "serve", 28);
    reader =
        //        new MnistReader(
        //            "data/t10k-labels-idx1-ubyte.idx1-ubyte",
        // "data/t10k-images-idx3-ubyte.idx3-ubyte");
        new MnistReader(
            "data/train-labels-idx1-ubyte.idx1-ubyte", "data/train-images-idx3-ubyte.idx3-ubyte");
  }

  @DataProvider(name = "trainDt")
  public Iterator<Object[]> provideMnistTrainData() {
    return reader;
  }

  @Test(
    dataProvider = "trainDt",
    //    timeOut = 4,
    successPercentage = 90
  )
  public void testRecognize(byte[] imgData, int label) {
    Assert.assertEquals(reco.predict(imgData, true), label);
  }

  @AfterClass
  public void close() throws Exception {
    reco.close();
  }
}
