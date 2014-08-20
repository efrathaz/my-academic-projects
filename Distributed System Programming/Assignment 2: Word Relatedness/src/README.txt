ASSIGNMENT DESCRIPTION:

The program extracts pairs of words that are related to each other from the Google 5-grams dataset, using Amazon Elastic Map Reduce.
It then calculates:
  * c(w) - the number of times w occurred in the extracted pairs, for each w.
  * c(w1,w2) - the number of times an extracted pair contained both w1 and w2, in any order.
  * N - the total number of word pairs in the corpus.

Finally it calculates 3 measures for every pair of words:
  1. The Joint Probability - for every w1, w2:  p(w1,w2) = c(w1,w2)/N.
  2. Dice Coefficient - for every w1, w2:  Dice(w1,w2) = 2*c(w1,w2) / (c(w1)+c(w2)).
  3. The Geometric Mean -  for every w1, w2:  GeometricMean(w1,w2) = sqrt(p(w1,w2)*Dice(w1,w2)).

The program gets a parameter K and outputs the K pairs with the highest score in each measure in each decade.


System Architecture:

The program consists of 4 map reduce jobs:
  * Step 1 - Parses the 5-grams, deletes Stop Words and removes non-letters characters. 
    Then, creates pairs from the middle (third) word with the other words.
    Calculates N, and c(w1,w2) for every pair.
  * Step 2 and Step 3 - Claculates c(w1) and c(w2) for every pair (w1,w2).
  * Step 4 - Calculates the 3 measures and returns the K pairs with the highest score for each measure.
Every reducer handles its own decade.


