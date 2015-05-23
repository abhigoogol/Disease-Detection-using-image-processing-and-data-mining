%	featurevector.m and this program creates the feature vector. Copyright
%	(C) 2014	Abhishek Singh Sambyal	email:abhishek.sambyal@gmail.com

%	This program is free software: you can redistribute it and/or modify it
%	under the terms of the GNU General Public License as published by the
%	Free Software Foundation, either version 3 of the License, or (at your
%	option) any later version.

%	This program is distributed in the hope that it will be useful, but
%	WITHOUT ANY WARRANTY; without even the implied warranty of
%	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
%	General Public License for more details.

%	You should have received a copy of the GNU General Public License along
%	with this program.  If not, see <http://www.gnu.org/licenses/>.







% This code has two sections i.e. Section 1 and Section 2.

%% Section 1
%  It contains the previously created feature vector "vector" on the
%  medical images given in folder "Brain MRI Images". If you want to work
%  on feature vector "vector" you can run this code as it is else you can
%  train this system for new data set by commenting the below three lines
%  and uncommenting the Section 2.

% clear all; close all; load vector.mat;




%% Section 2
%  It contains the code for training the system for a new feature vector.
%  It is advised not to run this code before you get familiar to it. Once
%  you feel comfortable you can train your system for your data set and
%  save it an another variable "value". To run this section, uncomment the
%  "Comment or Uncomment block"

%  Comment or Uncomment block
%%{


total_image = 43;			% Total number of images taken in a data set
total_feature = 140;		% Total number feature we want to extract

%	This is a path where the images are kept Change the source written in
%	quotes to the desired location of the images in your system

Source = '..\1BI12SCS01 - ABHISHEK CODE\Brain MRI Images dataset\';

%	Declarations and initializations

arra = cell(1,4);
arra_cell = cell(1,total_image);
arra_comb = cell(1,5);
arra_total = zeros(1,total_feature);
value = cell(1,3);

%	Finding feature vector of all the images

for i = 1:1%total_image
    path = strcat(Source,int2str(i),'.jpg');
    
    %	Read an image

    a = imread(path);
    
    % 	Convert each image

    b = rgb2gray(a);
    
    %	Resize image

    resize_img = imresize(b, [256 256], 'nearest');
    
    %	Find threshold

    thresh = multithresh(resize_img, 15);
    
    %	Quantize image using threshold

    quantizeImage = imquantize(resize_img, thresh);    

    name = strcat('IMG', int2str(i));
    n = 1;
    

    %	Each distance 1 to 5

    for dist = 1:1%5
    
    %	Co-occurrence matrix

        cooccurenceImage = cooccurrence (quantizeImage, 0, dist, 1);
        
    %	Features extraction

        x = GLCM_Features4(cooccurenceImage,0);
        
    %	Storing in array arra

        arra{1} = {x.step x.contr x.entro x.energ x.homom x.momen x.corrm};
        m = 2;
        
        %	Every direction

        for dir = 7:-1:6
        
        %	Co-occurrence matrix

            cooccurenceImage = cooccurrence (quantizeImage, dir, dist, 1);
        
        % Features extraction

            x = GLCM_Features4(cooccurenceImage,0);
        
        %	Storing in array arra

            arra{m} = {x.step x.contr x.entro x.energ x.homom x.momen x.corrm};
            m = m+1;
        end
        
        %	Combining in array arra_comb

        arra_comb{n} = [arra{1} arra{2} arra{3} arra{4}];
        n = n+1;
    end
    
    %	Combining the arrays

    arra_cell{i} = [arra_comb{1} arra_comb{2} arra_comb{3} arra_comb{4} arra_comb{5}];
    arra_total = cell2mat(arra_cell{i});
    
    
    %	 Formatting the data

    value{i,1} = name;
    value{i,2} = arra_total;
    value{i,3} = 1;
end

%}
