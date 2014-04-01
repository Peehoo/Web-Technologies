<HTML> 
	<HEAD><TITLE>Market Stock Search</TITLE></HEAD> 
		<BODY> 

		<?php
		error_reporting(0);
		$data = "";
		$news = "";
		function getXML($stock)
        {
          $url = "http://query.yahooapis.com/v1/public/yql?q=Select%20Name%2C%20Symbol%2C%20LastTradePriceOnly%2C%20Change%2C%20ChangeinPercent%2C%20PreviousClose%2C%20DaysLow%2C%20DaysHigh%2C%20Open%2C%20YearLow%2C%20YearHigh%2C%20Bid%2C%20Ask%2C%20AverageDailyVolume%2C%20OneyrTargetPrice%2C%20MarketCapitalization%2C%20Volume%2C%20Open%2C%20YearLow%20from%20yahoo.finance.quotes%20where%20symbol%3D%22". urlencode($stock) ."%22&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"; 
          $data = file_get_contents($url); 
          $data = new SimpleXmlElement(utf8_encode($data));
          return $data;
        }
		function getNews($stock)
        {
          $url = "http://feeds.finance.yahoo.com/rss/2.0/headline?s=". urlencode($stock) ."&region=US&lang=en-US";
          $news = file_get_contents($url);
          $news = new SimpleXmlElement($news);
          return $news;
        }  
		?>
	      <?php 
			if (isset($_GET["submit"])):{
              $stock = $_GET["input"];  
				if($stock==""){
					echo "<script language='javascript'>alert('Please enter a company symbol');
					window.location = window.location.pathname;</script>";
				}
				else {
					$data = getXML($stock);
                  	$news = getNews($stock);
				}
			}
	      ?>
	  
         <?php if(strcmp((string)$data->results->quote->Open , "") == 0){?>
             <H1 style="text-align:center;">Market Stock Search</H1> 
             <FORM ACTION="" METHOD=GET style="border-style:solid; border-color:black; margin-left: 435px; margin-right: 400px; padding-left: 5px; padding-top:0px;"> 
                 <P style="text-align:left;">Company Symbol: <INPUT NAME=input VALUE="<?php echo $stock ?>" size="35"/>
                     <INPUT TYPE=submit name="submit" value="Search"/><BR>
                     Example:<i> GOOG, MSFT, YHOO, FB, AAPL, ...etc</i>
                 </P>
             </FORM>
             <H1 style="text-align:center;">Stock Information not available</H1>
            <?php break;?>
            <?php 
         }
         ?>
          
         <H1 style="text-align:center;">Market Stock Search</H1> 
		 <FORM ACTION="" METHOD=GET style="border-style:solid; border-color:black; margin-left: 435px; margin-right: 400px; padding-left: 5px; padding-top:0px;"> 
           <P style="text-align:left;">Company Symbol: <INPUT NAME=input size="35"/>
             <INPUT TYPE=submit name="submit" value="Search"/><BR>
             Example:<i> GOOG, MSFT, YHOO, FB, AAPL, ...etc</i>
           </P>
		 </FORM> 
		 <H1 style="text-align:center;">Search Results</H1> 
		
          <table width="700" align="center"}>
		  <tr>
		    <th style="font-size:150%; text-align:left;"><?php echo (string)$data->results->quote->Name ; 
				 echo "(" . (string)$data->results->quote->Symbol . ")"; ?></th>
		    <td style="font-size:110%; text-align:left; vertical-align:bottom;"><?php echo number_format((string)$data->results->quote->LastTradePriceOnly,2) ; 
				 ?></td> <td style="font-size:110%; text-align:left; vertical-align:bottom;"><?php  $Change = (float)($data->results->quote->Change);
		    	 if($Change<0){
		    	 	?><a style="color:red";><img src="http://www-scf.usc.edu/~csci571/2014Spring/hw6/down_r.gif" /><?php echo $Change*-1;
		    		echo "(" . substr((string)$data->results->quote->ChangeinPercent,1) . ")" ;
		    	 }
		    	 else if ($Change>0){
		    	 	?></a><a style="color:green";><img src="http://www-scf.usc.edu/~csci571/2014Spring/hw6/up_g.gif" /><?php echo $Change;
		    		echo "(" . substr((string)$data->results->quote->ChangeinPercent,1) . ")" ;
		    	 }
				 else if ($Change==0){
		    	 	?></a><a style="color:green";><?php echo $Change;
		    		echo "(" . substr((string)$data->results->quote->ChangeinPercent,1) . ")" ;
		    	 }
		    	 ?></a></td>
		    <td width="250"></td>
		  </tr>
		 </table>
          
		<hr color="black" width="700" align="center">
		
         <table width="700" align="center">
         <tr>
            <td width = "20%" style="padding-right: 0px;">Prev Close:</td>
            <td width = "20%" style="padding-left: 100px;">
              <?php echo number_format((string)$data->results->quote->PreviousClose,2);?>
            </td>
            <td width = "25%" style="text-align:left;padding-left: 25px;">Day's Range:</td>
            <td width = "35%" style="text-align:right;"><?php echo number_format((string)$data->results->quote->DaysLow,2). " - " . number_format((string)$data->results->quote->DaysHigh,2);?></td>
         </tr>
         <tr>
            <td width = "20%" style="padding-right: 0px;">Open:</td>
            <td width = "20%" style="padding-left: 100px;">
              <?php echo number_format((string)$data->results->quote->Open,2);?>
            </td>
            <td width = "25%" style="text-align:left;padding-left: 25px;">52wk Range:</td>
            <td width = "35%" style="text-align:right;"><?php echo number_format((string)$data->results->quote->YearLow,2). " - " . number_format((string)$data->results->quote->YearHigh,2);?></td>
		 </tr>
         <tr>
            <td width = "20%" style="padding-right: 0px;">Bid:</td>
            <td width = "20%" style="padding-left: 100px;">
              <?php echo number_format((string)$data->results->quote->Bid,2);?>
            </td>
            <td width = "25%" style="text-align:left;padding-left: 25px;">Volume:</td>
            <td width = "35%" style="text-align:right;"><?php echo number_format((string)$data->results->quote->Volume)?></td>
		</tr>
        <tr>
            <td width = "20%" style="padding-right: 0px;">Ask:</td>
            <td width = "20%" style="padding-left: 100px;">
              <?php echo number_format((string)$data->results->quote->Ask,2);?>
            </td>
            <td width = "25%" style="text-align:left;padding-left: 25px;">Avg Vol(3m):</td>
            <td width = "35%" style="text-align:right;"><?php echo number_format((string)$data->results->quote->AverageDailyVolume)?></td>
		</tr>
        <tr>
            <td width = "20%" style="padding-right: 0px;">1y Target Est:</td>
            <td width = "20%" style="padding-left: 100px;">
              <?php echo number_format((string)$data->results->quote->OneyrTargetPrice,2);?>
            </td>
            <td width = "25%" style="text-align:left;padding-left: 25px;">Market Cap:</td>
            <td width = "35%" style="text-align:right;"><?php echo (string)$data->results->quote->MarketCapitalization?></td>
		</tr> 
		</table>
        

        <table width="700" align="center">
          <tr><th style="font-size:150%; text-align:left;">News Headlines</th></tr>
        </table>
        
          <hr color="black" width="700" align="center">
          <?php $array = $news->channel->item;?>
          
          <table width="700" align="center">
            <tr>
              <td>
                <ul>
                  <?php for($x=0;$x<count($array);$x++)
                 {?><li><a href="<?php echo $array[$x]->link ?>"><?php
				   echo htmlentities($array[$x]->title);
                   echo "<br>";
                    ?></a></li><?php
                 }
                  ?>
                </ul>
              </td>
            </tr>
          </table>

			<?php else: ?> 
				 <H1 style="text-align:center;">Market Stock Search</H1> 
				 <FORM ACTION="" METHOD=GET style="border-style:solid; border-color:black; margin-left: 435px; margin-right: 400px; padding-left: 5px; padding-top:0px;"> 
					 <P style="text-align:left;">Company Symbol: <INPUT NAME=input size="35"/>
						 <INPUT TYPE=submit name="submit" value="Search"/><BR>
						 Example:<i> GOOG, MSFT, YHOO, FB, AAPL, ...etc</i>
					 </P>
				 </FORM> 
			<?php endif; ?> 
		</BODY> 
</HTML>